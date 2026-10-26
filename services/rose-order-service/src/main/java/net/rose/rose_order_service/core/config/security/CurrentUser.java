package net.rose.rose_order_service.core.config.security;

import net.rose.rose_order_service.core.persistence.id.UserId;

public record CurrentUser(
        UserId userId,
        String email,
        String accessToken
) {
}