package net.rose.rose_identity_service.auth.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.auth.domain.service.api.RefreshTokenService;
import net.rose.rose_identity_service.auth.persistence.entity.RefreshTokenEntity;
import net.rose.rose_identity_service.auth.persistence.repository.api.RefreshTokenRepository;
import net.rose.rose_identity_service.core.exception.InvalidTokenException;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultRefreshTokenService implements RefreshTokenService {

    private final RefreshTokenRepository repository;


    private final VerificationTokenGenerator tokenGenerator;


    private final TokenHashService tokenHashService;


    @Value("${app.security.jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;


    @Override
    @Transactional
    public String create(UserId userId) {
        String rawToken = tokenGenerator.generate();
        String tokenHash = tokenHashService.hash(rawToken);

        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusSeconds(refreshTokenExpirationDays * 24 * 60 * 60))
                .build();

        repository.save(entity);

        return rawToken;
    }


    @Override
    @Transactional
    public UserId validateAndGetUserId(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw InvalidTokenException.missing();
        }

        String tokenHash = tokenHashService.hash(rawToken);

        RefreshTokenEntity entity = repository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::invalid);

        if (entity.isRevoked()) {
            throw InvalidTokenException.invalid();
        }

        if (entity.isExpired()) {
            throw InvalidTokenException.expired();
        }

        entity.setLastUsedAt(Instant.now());

        return entity.getUserId();
    }


    @Override
    @Transactional
    public void revoke(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return;
        }

        String tokenHash = tokenHashService.hash(rawToken);

        repository.findByTokenHash(tokenHash)
                .filter(token -> !token.isRevoked())
                .ifPresent(token -> token.setRevokedAt(Instant.now()));
    }


    @Override
    @Transactional
    public void revokeAllForUser(UserId userId) {
        repository.findAllByUserId(userId)
                .stream()
                .filter(RefreshTokenEntity::isActive)
                .forEach(entity -> entity.setRevokedAt(Instant.now()));
    }

}