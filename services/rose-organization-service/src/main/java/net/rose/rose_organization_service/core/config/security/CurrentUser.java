package net.rose.rose_organization_service.core.config.security;

import net.rose.rose_organization_service.core.persistence.id.UserId;

public record CurrentUser(
        UserId userId,
        String email
) {
}
