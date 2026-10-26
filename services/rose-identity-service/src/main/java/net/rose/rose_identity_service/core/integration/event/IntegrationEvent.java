package net.rose.rose_identity_service.core.integration.event;

import java.time.Instant;
import java.util.UUID;

public record IntegrationEvent<T>(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        String producer,
        UUID correlationId,
        T payload
) {

    public IntegrationEvent {
        if (eventId == null) {
            throw new IllegalArgumentException("Event id cannot be null");
        }

        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("Event type cannot be blank");
        }

        if (eventVersion < 1) {
            throw new IllegalArgumentException(
                    "Event version must be greater than zero"
            );
        }

        if (occurredAt == null) {
            throw new IllegalArgumentException("Occurred at cannot be null");
        }

        if (producer == null || producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be blank");
        }

        if (correlationId == null) {
            throw new IllegalArgumentException(
                    "Correlation id cannot be null"
            );
        }

        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
    }
}