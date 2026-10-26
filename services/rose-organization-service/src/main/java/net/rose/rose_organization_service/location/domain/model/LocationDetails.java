package net.rose.rose_organization_service.location.domain.model;

import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;

import java.time.Instant;

public record LocationDetails(
        LocationId id,
        RestaurantId restaurantId,
        String name,
        String city,
        String street,
        String buildingNumber,
        String postalCode,
        LocationStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}