package net.rose.rose_menu_service.menu.rest.dto;

import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;

import java.time.Instant;
import java.util.UUID;

public record MenuCategoryReadDto(
        UUID id,
        UUID menuId,
        String name,
        Integer displayOrder,
        MenuCategoryStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}