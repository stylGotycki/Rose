package net.rose.rose_identity_service.core.integration.inbox.persistence.repository.api;

import net.rose.rose_identity_service.core.integration.inbox.persistence.entity.InboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxMessageRepository
        extends JpaRepository<InboxMessageEntity, UUID> {
}