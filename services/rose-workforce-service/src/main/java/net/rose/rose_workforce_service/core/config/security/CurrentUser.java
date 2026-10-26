package net.rose.rose_workforce_service.core.config.security;

import net.rose.rose_workforce_service.core.persistence.id.UserId;

public record CurrentUser(
        UserId userId,
        String email
) {
}
