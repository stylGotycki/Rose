package net.rose.rose_workforce_service.core.integration.outbox.publisher;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.core.integration.outbox.domain.model.OutboxEventStatus;
import net.rose.rose_workforce_service.core.integration.outbox.persistence.repository.api.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository repository;

    private final OutboxEventPublishingService publishingService;

    @Scheduled(
            fixedDelayString =
                    "${rose.messaging.outbox.fixed-delay-ms:1000}"
    )
    public void publishPendingEvents() {
        List<UUID> eventIds = repository
                .findTop50ByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
                        OutboxEventStatus.PENDING,
                        Instant.now()
                )
                .stream()
                .map(event -> event.getEventId())
                .toList();

        eventIds.forEach(publishingService::publish);
    }
}