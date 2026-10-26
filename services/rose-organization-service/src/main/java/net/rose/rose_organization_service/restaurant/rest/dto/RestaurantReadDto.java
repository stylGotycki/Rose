package net.rose.rose_organization_service.restaurant.rest.dto;

import net.rose.rose_organization_service.restaurant.domain.model.RestaurantStatus;

import java.time.Instant;
import java.util.UUID;

public record RestaurantReadDto(
        UUID id,
        String name,
        String description,
        String taxId,
        RestaurantStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}