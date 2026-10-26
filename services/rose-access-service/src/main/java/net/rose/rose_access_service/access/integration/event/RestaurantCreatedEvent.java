package net.rose.rose_access_service.access.integration.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantCreatedEvent(
        UUID restaurantId,
        UUID ownerUserId,
        String name,
        Instant createdAt
) {
}