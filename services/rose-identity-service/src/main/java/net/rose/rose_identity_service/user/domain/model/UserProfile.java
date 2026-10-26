package net.rose.rose_identity_service.user.domain.model;

import net.rose.rose_identity_service.core.persistence.id.UserId;


public record UserProfile(
        UserId id,
        String firstName,
        String lastName,
        String email,
        UserStatus status
) {
}
