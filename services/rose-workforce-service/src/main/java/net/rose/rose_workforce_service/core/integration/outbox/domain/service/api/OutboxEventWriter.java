package net.rose.rose_workforce_service.core.integration.outbox.domain.service.api;

import net.rose.rose_workforce_service.core.integration.event.IntegrationEvent;

public interface OutboxEventWriter {

    void write(
            String exchange,
            String routingKey,
            IntegrationEvent<?> event
    );
}