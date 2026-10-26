package net.rose.rose_billing_service.billing.integration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_billing_service.billing.domain.command.CreateBillFromCompletedOrderCommand;
import net.rose.rose_billing_service.billing.domain.service.api.BillCommandService;
import net.rose.rose_billing_service.core.integration.event.IntegrationEvent;
import net.rose.rose_billing_service.core.integration.inbox.domain.model.InboxProcessingResult;
import net.rose.rose_billing_service.core.integration.inbox.domain.service.api.InboxEventProcessor;
import net.rose.rose_billing_service.core.persistence.id.OrderId;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;
import net.rose.rose_billing_service.order.integration.event.OrderCompletedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class OrderCompletedListener {

    private static final String SUPPORTED_EVENT_TYPE =
            "order.completed";

    private static final int SUPPORTED_EVENT_VERSION = 1;

    private final BillCommandService billCommandService;
    private final InboxEventProcessor inboxEventProcessor;

    @RabbitListener(
            queues = "${rose.messaging.order.completed-queue}"
    )
    void on(
            @Payload
            IntegrationEvent<OrderCompletedEvent> event
    ) {
        validate(event);

        OrderCompletedEvent payload = event.payload();

        InboxProcessingResult result =
                inboxEventProcessor.process(
                        event,
                        () -> billCommandService
                                .createFromCompletedOrder(
                                        new CreateBillFromCompletedOrderCommand(
                                                OrderId.of(
                                                        payload.orderId()
                                                ),
                                                RestaurantId.of(
                                                        payload.restaurantId()
                                                ),
                                                payload.totalAmount(),
                                                payload.currency()
                                        )
                                )
                );

        if (result == InboxProcessingResult.ALREADY_PROCESSED) {
            log.info(
                    "Skipped already processed completed order event: eventId={}",
                    event.eventId()
            );
            return;
        }

        log.info(
                "Created bill from completed order: eventId={}, orderId={}, restaurantId={}, totalAmount={}, currency={}",
                event.eventId(),
                payload.orderId(),
                payload.restaurantId(),
                payload.totalAmount(),
                payload.currency()
        );
    }

    private void validate(
            IntegrationEvent<OrderCompletedEvent> event
    ) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Completed order event cannot be null"
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