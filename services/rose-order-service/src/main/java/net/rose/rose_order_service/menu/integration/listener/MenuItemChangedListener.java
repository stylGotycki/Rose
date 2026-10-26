package net.rose.rose_order_service.menu.integration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_order_service.core.integration.event.IntegrationEvent;
import net.rose.rose_order_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_order_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import net.rose.rose_order_service.menu.domain.service.api.MenuItemProjectionService;
import net.rose.rose_order_service.menu.integration.event.MenuItemChangedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class MenuItemChangedListener {

    private static final String SUPPORTED_EVENT_TYPE =
            "menu.item-changed";

    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final MenuItemProjectionService projectionService;
    private final InboxEventProcessor inboxEventProcessor;

    @RabbitListener(
            queues = "${rose.messaging.menu.item-changed-queue}"
    )
    void on(
            @Payload
            IntegrationEvent<MenuItemChangedEvent> event
    ) {
        validate(event);

        InboxProcessingResult result =
                inboxEventProcessor.process(
                        event,
                        () -> projectionService.apply(
                                event.payload()
                        )
                );

        if (result == InboxProcessingResult.ALREADY_PROCESSED) {
            log.info(
                    "Skipped already processed menu item event: eventId={}",
                    event.eventId()
            );
            return;
        }

        log.info(
                "Updated menu item projection: eventId={}, menuItemId={}, restaurantId={}, orderable={}",
                event.eventId(),
                event.payload().menuItemId(),
                event.payload().restaurantId(),
                event.payload().orderable()
        );
    }

    private void validate(
            IntegrationEvent<MenuItemChangedEvent> event
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Menu item integration event cannot be null"
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