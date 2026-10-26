package net.rose.rose_organization_service.restaurant.domain.model;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;

import java.time.Instant;

public record RestaurantDetails(
        RestaurantId id,
        String name,
        String description,
        String taxId,
        RestaurantStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}