package net.rose.rose_menu_service.menu.rest.dto;

import net.rose.rose_menu_service.menu.domain.model.MenuStatus;

import java.time.Instant;
import java.util.UUID;

public record MenuReadDto(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        MenuStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}