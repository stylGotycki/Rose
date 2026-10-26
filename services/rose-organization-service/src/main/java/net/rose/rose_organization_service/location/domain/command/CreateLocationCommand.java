package net.rose.rose_organization_service.location.domain.command;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;

public record CreateLocationCommand(
        RestaurantId restaurantId,
        String name,
        String city,
        String street,
        String buildingNumber,
        String postalCode
) {
}