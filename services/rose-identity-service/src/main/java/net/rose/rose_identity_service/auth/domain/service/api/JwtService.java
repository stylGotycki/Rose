package net.rose.rose_identity_service.auth.domain.service.api;

import net.rose.rose_identity_service.core.persistence.id.UserId;

import java.time.Instant;

public interface JwtService {

    String generateAccessToken(UserId userId, String email);

    UserId extractUserId(String token);

    String extractEmail(String token);

    String extractTokenId(String token);

    Instant extractExpiration(String token);

    boolean isValid(String token);

    boolean isRevoked(String token);

    void revoke(String token);
}