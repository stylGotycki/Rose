package net.rose.rose_organization_service.core.integration.inbox.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.core.integration.event.IntegrationEvent;
import net.rose.rose_organization_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_organization_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import net.rose.rose_organization_service.core.integration.inbox.persistence.entity.InboxMessageEntity;
import net.rose.rose_organization_service.core.integration.inbox.persistence.repository.api.InboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultInboxEventProcessor implements InboxEventProcessor {

    private final InboxMessageRepository repository;

    @Override
    @Transactional
    public InboxProcessingResult process(
            IntegrationEvent<?> event,
            Runnable eventHandler
    ) {
        validate(event, eventHandler);

        if (repository.existsById(event.eventId())) {
            return InboxProcessingResult.ALREADY_PROCESSED;
        }

        eventHandler.run();

        InboxMessageEntity inboxMessage =
                InboxMessageEntity.builder()
                        .eventId(event.eventId())
                        .eventType(event.eventType())
                        .eventVersion(event.eventVersion())
                        .producer(event.producer())
                        .correlationId(event.correlationId())
                        .occurredAt(event.occurredAt())
                        .processedAt(Instant.now())
                        .build();

        repository.saveAndFlush(inboxMessage);

        return InboxProcessingResult.PROCESSED;
    }

    private void validate(
            IntegrationEvent<?> event,
            Runnable eventHandler
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Integration event cannot be null"
            );
        }

        if (event.eventId() == null) {
            throw new IllegalArgumentException(
                    "Event id cannot be null"
            );
        }

        if (event.eventType() == null || event.eventType().isBlank()) {
            throw new IllegalArgumentException(
                    "Event type cannot be blank"
            );
        }

        if (event.eventVersion() < 1) {
            throw new IllegalArgumentException(
                    "Event version must be greater than zero"
            );
        }

        if (event.producer() == null || event.producer().isBlank()) {
            throw new IllegalArgumentException(
                    "Event producer cannot be blank"
            );
        }

        if (event.correlationId() == null) {
            throw new IllegalArgumentException(
                    "Correlation id cannot be null"
            );
        }

        if (event.occurredAt() == null) {
            throw new IllegalArgumentException(
                    "Event occurredAt cannot be null"
            );
        }

        if (eventHandler == null) {
            throw new IllegalArgumentException(
                    "Event handler cannot be null"
            );
        }
    }
}