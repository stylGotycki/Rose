package net.rose.rose_order_service.access.integration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_order_service.access.domain.service.api.RestaurantAccessProjectionService;
import net.rose.rose_order_service.access.integration.event.RestaurantMemberChangedEvent;
import net.rose.rose_order_service.core.integration.event.IntegrationEvent;
import net.rose.rose_order_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_order_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RestaurantMemberChangedListener {

    private static final String SUPPORTED_EVENT_TYPE =
            "access.restaurant-member-changed";

    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final RestaurantAccessProjectionService projectionService;

    private final InboxEventProcessor inboxEventProcessor;

    @RabbitListener(
            queues = "${rose.messaging.access.member-changed-queue}"
    )
    void on(
            @Payload
            IntegrationEvent<RestaurantMemberChangedEvent> event
    ) {
        validate(event);

        InboxProcessingResult result =
                inboxEventProcessor.process(
                        event,
                        () -> projectionService.apply(event.payload())
                );

        if (result == InboxProcessingResult.ALREADY_PROCESSED) {
            log.info(
                    "Skipped already processed restaurant member event: eventId={}",
                    event.eventId()
            );
            return;
        }

        log.info(
                "Updated restaurant access projection: eventId={}, restaurantId={}, userId={}, role={}, status={}",
                event.eventId(),
                event.payload().restaurantId(),
                event.payload().userId(),
                event.payload().role(),
                event.payload().status()
        );
    }

    private void validate(
            IntegrationEvent<RestaurantMemberChangedEvent> event
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Restaurant member integration event cannot be null"
            );
        }

        if (!SUPPORTED_EVENT_TYPE.equals(event.eventType())) {
            throw new IllegalArgumentException(
                    "Unsupported event type: " + event.eventType()
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