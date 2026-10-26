package net.rose.rose_menu_service.access.integration.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantMemberChangedEvent(
        UUID memberId,
        UUID restaurantId,
        UUID userId,
        String role,
        String status,
        Instant changedAt
) {
}