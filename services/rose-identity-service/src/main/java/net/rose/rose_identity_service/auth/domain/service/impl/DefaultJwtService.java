package net.rose.rose_identity_service.auth.domain.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.rose.rose_identity_service.auth.domain.service.api.JwtService;
import net.rose.rose_identity_service.auth.persistence.entity.RevokedAccessTokenEntity;
import net.rose.rose_identity_service.auth.persistence.repository.api.RevokedAccessTokenRepository;
import net.rose.rose_identity_service.core.exception.InvalidTokenException;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class DefaultJwtService implements JwtService {

    private final SecretKey secretKey;


    private final long accessTokenExpirationMinutes;


    private final RevokedAccessTokenRepository revokedAccessTokenRepository;


    public DefaultJwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-token-expiration-minutes}") long accessTokenExpirationMinutes,
            RevokedAccessTokenRepository revokedAccessTokenRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
    }


    @Override
    public String generateAccessToken(UserId userId, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpirationMinutes * 60);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(userId.getId().toString())
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }


    @Override
    public UserId extractUserId(String token) {
        Claims claims = parseClaims(token);

        return UserId.of(UUID.fromString(claims.getSubject()));
    }


    @Override
    public String extractEmail(String token) {
        Claims claims = parseClaims(token);

        return claims.get("email", String.class);
    }


    @Override
    public String extractTokenId(String token) {
        Claims claims = parseClaims(token);

        return claims.getId();
    }


    @Override
    public Instant extractExpiration(String token) {
        Claims claims = parseClaims(token);

        return claims.getExpiration().toInstant();
    }


    @Override
    public boolean isValid(String token) {
        try {
            parseClaims(token);

            return !isRevoked(token);
        } catch (RuntimeException exception) {
            return false;
        }
    }


    @Override
    public boolean isRevoked(String token) {
        String tokenId = extractTokenId(token);

        if (tokenId == null || tokenId.isBlank()) {
            return true;
        }

        return revokedAccessTokenRepository.existsByTokenId(tokenId);
    }


    @Override
    @Transactional
    public void revoke(String token) {
        Claims claims = parseClaims(token);

        String tokenId = claims.getId();

        if (tokenId == null || tokenId.isBlank()) {
            throw InvalidTokenException.invalid();
        }

        if (revokedAccessTokenRepository.existsByTokenId(tokenId)) {
            return;
        }

        RevokedAccessTokenEntity entity = RevokedAccessTokenEntity.builder()
                .tokenId(tokenId)
                .expiresAt(claims.getExpiration().toInstant())
                .build();

        revokedAccessTokenRepository.save(entity);
    }


    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (RuntimeException exception) {
            throw InvalidTokenException.invalid();
        }
    }

}