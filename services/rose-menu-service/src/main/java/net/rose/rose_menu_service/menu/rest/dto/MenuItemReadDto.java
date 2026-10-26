package net.rose.rose_menu_service.menu.rest.dto;

import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MenuItemReadDto(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String currency,
        Boolean available,
        MenuItemStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}