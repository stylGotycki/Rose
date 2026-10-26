package net.rose.rose_identity_service.user.integration.event;

import java.time.Instant;
import java.util.UUID;

public record RestaurantAccessGrantedEvent(
        UUID memberId,
        UUID restaurantId,
        UUID userId,
        String role,
        Instant grantedAt
) {

    public RestaurantAccessGrantedEvent {
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
                    "Role cannot be blank"
            );
        }

        if (grantedAt == null) {
            throw new IllegalArgumentException(
                    "Granted at cannot be null"
            );
        }
    }
}