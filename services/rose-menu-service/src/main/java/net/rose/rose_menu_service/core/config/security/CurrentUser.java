package net.rose.rose_menu_service.core.config.security;

import net.rose.rose_menu_service.core.persistence.id.UserId;

public record CurrentUser(
        UserId userId,
        String email
) {
}
