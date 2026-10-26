package net.rose.rose_organization_service.restaurant.integration.event;

import net.rose.rose_organization_service.core.integration.event.IntegrationEvent;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.core.persistence.id.UserId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class RestaurantCreatedIntegrationEventFactory {

    private static final String EVENT_TYPE =
            "organization.restaurant-created";

    private static final int EVENT_VERSION = 1;

    private static final String PRODUCER =
            "rose-layer-1";

    public IntegrationEvent<RestaurantCreatedIntegrationEvent> create(
            RestaurantId restaurantId,
            UserId ownerUserId,
            String restaurantName
    ) {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        RestaurantCreatedIntegrationEvent payload =
                new RestaurantCreatedIntegrationEvent(
                        restaurantId.getId(),
                        ownerUserId.getId(),
                        restaurantName,
                        occurredAt
                );

        return new IntegrationEvent<>(
                eventId,
                EVENT_TYPE,
                EVENT_VERSION,
                occurredAt,
                PRODUCER,
                eventId,
                payload
        );
    }
}