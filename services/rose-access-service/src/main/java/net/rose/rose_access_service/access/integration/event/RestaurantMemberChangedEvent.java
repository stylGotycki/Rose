package net.rose.rose_access_service.access.integration.event;

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

    public RestaurantMemberChangedEvent {
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "Member id cannot be null"
            );
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id cannot be null"
            );
        }

        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException(
                    "Member role cannot be blank"
            );
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Member status cannot be blank"
            );
        }

        if (changedAt == null) {
            throw new IllegalArgumentException(
                    "Changed at cannot be null"
            );
        }
    }
}