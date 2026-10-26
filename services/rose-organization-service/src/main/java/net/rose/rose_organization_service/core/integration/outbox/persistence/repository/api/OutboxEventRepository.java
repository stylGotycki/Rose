package net.rose.rose_organization_service.core.integration.outbox.persistence.repository.api;

import net.rose.rose_organization_service.core.integration.outbox.domain.model.OutboxEventStatus;
import net.rose.rose_organization_service.core.integration.outbox.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity>
    findTop50ByStatusAndNextAttemptAtLessThanEqualOrderByOccurredAtAsc(
            OutboxEventStatus status,
            Instant nextAttemptAt
    );
}