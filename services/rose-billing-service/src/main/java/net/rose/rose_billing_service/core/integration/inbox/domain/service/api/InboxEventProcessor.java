package net.rose.rose_billing_service.core.integration.inbox.domain.service.api;

import net.rose.rose_billing_service.core.integration.event.IntegrationEvent;
import net.rose.rose_billing_service.core.integration.inbox.domain.model.InboxProcessingResult;

public interface InboxEventProcessor {

    InboxProcessingResult process(
            IntegrationEvent<?> event,
            Runnable eventHandler
    );
}