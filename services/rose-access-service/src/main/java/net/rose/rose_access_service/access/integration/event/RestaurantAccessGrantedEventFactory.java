package net.rose.rose_access_service.access.integration.event;

import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.integration.event.IntegrationEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class RestaurantAccessGrantedEventFactory {

    private static final String EVENT_TYPE =
            "access.restaurant-access-granted";

    private static final int EVENT_VERSION = 1;

    private static final String PRODUCER =
            "rose-access-service";

    public IntegrationEvent<RestaurantAccessGrantedEvent> create(
            RestaurantMemberEntity member
    ) {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        RestaurantAccessGrantedEvent payload =
                new RestaurantAccessGrantedEvent(
                        member.getId(),
                        member.getRestaurantId().getId(),
                        member.getUserId().getId(),
                        member.getRole().name(),
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