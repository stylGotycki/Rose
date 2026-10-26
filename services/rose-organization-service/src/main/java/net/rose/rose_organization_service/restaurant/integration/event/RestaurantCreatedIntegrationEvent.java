package net.rose.rose_organization_service.restaurant.integration.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantCreatedIntegrationEvent(
        UUID restaurantId,
        UUID ownerUserId,
        String name,
        Instant createdAt
) {

    public RestaurantCreatedIntegrationEvent {
        if (restaurantId == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (ownerUserId == null) {
            throw new IllegalArgumentException(
                    "Owner user id cannot be null"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Restaurant name cannot be blank"
            );
        }

        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Created at cannot be null"
            );
        }
    }
}