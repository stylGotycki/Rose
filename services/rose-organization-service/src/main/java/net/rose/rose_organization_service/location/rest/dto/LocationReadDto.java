package net.rose.rose_organization_service.location.rest.dto;

import net.rose.rose_organization_service.location.domain.model.LocationStatus;

import java.time.Instant;
import java.util.UUID;

public record LocationReadDto(
        UUID id,
        UUID restaurantId,
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