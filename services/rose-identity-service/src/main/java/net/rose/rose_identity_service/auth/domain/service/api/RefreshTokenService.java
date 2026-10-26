package net.rose.rose_identity_service.auth.domain.service.api;

import net.rose.rose_identity_service.core.persistence.id.UserId;

public interface RefreshTokenService {

    String create(UserId userId);


    UserId validateAndGetUserId(String rawToken);


    void revoke(String rawToken);


    void revokeAllForUser(UserId userId);

}