package net.rose.rose_access_service.access.integration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessCommandService;
import net.rose.rose_access_service.access.integration.event.RestaurantCreatedEvent;
import net.rose.rose_access_service.core.integration.event.IntegrationEvent;
import net.rose.rose_access_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_access_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RestaurantCreatedListener {

    private static final String SUPPORTED_EVENT_TYPE =
            "organization.restaurant-created";

    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final RestaurantAccessCommandService commandService;
    private final InboxEventProcessor inboxEventProcessor;

    @RabbitListener(
            queues = "${rose.messaging.organization.restaurant-created-queue}"
    )
    void on(
            @Payload
            IntegrationEvent<RestaurantCreatedEvent> event
    ) {
        validate(event);

        RestaurantCreatedEvent payload = event.payload();

        InboxProcessingResult result =
                inboxEventProcessor.process(
                        event,
                        () -> commandService.synchronizeOwner(
                                RestaurantId.of(
                                        payload.restaurantId()
                                ),
                                UserId.of(
                                        payload.ownerUserId()
                                )
                        )
                );

        if (result == InboxProcessingResult.ALREADY_PROCESSED) {
            log.info(
                    "Skipped already processed restaurant-created event: eventId={}",
                    event.eventId()
            );
            return;
        }

        log.info(
                "Created owner membership from restaurant event: eventId={}, restaurantId={}, ownerUserId={}",
                event.eventId(),
                payload.restaurantId(),
                payload.ownerUserId()
        );
    }

    private void validate(
            IntegrationEvent<RestaurantCreatedEvent> event
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Restaurant-created event cannot be null"
            );
        }

        if (!SUPPORTED_EVENT_TYPE.equals(event.eventType())) {
            throw new IllegalArgumentException(
                    "Unsupported event type: "
                            + event.eventType()
            );
        }

        if (event.eventVersion() != SUPPORTED_EVENT_VERSION) {
            throw new IllegalArgumentException(
                    "Unsupported event version: "
                            + event.eventVersion()
            );
        }

        if (event.payload() == null) {
            throw new IllegalArgumentException(
                    "Event payload cannot be null"
            );
        }
    }
}