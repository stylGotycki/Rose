package net.rose.rose_identity_service.auth.domain.event;

import net.rose.rose_identity_service.core.persistence.id.UserId;

public record PasswordResetTokenCreatedEvent(
        UserId userId,
        String email,
        String rawToken
) {
}