package net.rose.rose_billing_service.core.integration.outbox.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_billing_service.core.integration.event.IntegrationEvent;
import net.rose.rose_billing_service.core.integration.outbox.domain.model.OutboxEventStatus;
import net.rose.rose_billing_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import net.rose.rose_billing_service.core.integration.outbox.persistence.entity.OutboxEventEntity;
import net.rose.rose_billing_service.core.integration.outbox.persistence.repository.api.OutboxEventRepository;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
class DefaultOutboxEventWriter implements OutboxEventWriter {

    private final OutboxEventRepository repository;

    private final JsonMapper jsonMapper;

    @Override
    public void write(
            String exchange,
            String routingKey,
            IntegrationEvent<?> event
    ) {
        validate(exchange, routingKey, event);

        String serializedEvent = serialize(event);

        OutboxEventEntity entity =
                OutboxEventEntity.builder()
                        .eventId(event.eventId())
                        .eventType(event.eventType())
                        .eventVersion(event.eventVersion())
                        .exchangeName(exchange)
                        .routingKey(routingKey)
                        .payload(serializedEvent)
                        .status(OutboxEventStatus.PENDING)
                        .occurredAt(event.occurredAt())
                        .attempts(0)
                        .nextAttemptAt(event.occurredAt())
                        .build();

        repository.save(entity);
    }

    private String serialize(IntegrationEvent<?> event) {
        try {
            return jsonMapper.writeValueAsString(event);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Could not serialize integration event: "
                            + event.eventType(),
                    exception
            );
        }
    }

    private void validate(
            String exchange,
            String routingKey,
            IntegrationEvent<?> event
    ) {
        if (exchange == null || exchange.isBlank()) {
            throw new IllegalArgumentException(
                    "Exchange cannot be blank"
            );
        }

        if (routingKey == null || routingKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Routing key cannot be blank"
            );
        }

        if (event == null) {
            throw new IllegalArgumentException(
                    "Integration event cannot be null"
            );
        }
    }
}