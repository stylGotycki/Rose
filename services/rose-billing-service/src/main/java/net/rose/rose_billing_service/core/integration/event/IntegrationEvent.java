package net.rose.rose_billing_service.core.integration.event;

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
}