package net.rose.rose_order_service.order.integration.event;

import net.rose.rose_order_service.core.integration.event.IntegrationEvent;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
public class OrderCompletedEventFactory {

    private static final String EVENT_TYPE =
            "order.completed";

    private static final int EVENT_VERSION = 1;

    private static final String PRODUCER =
            "rose-layer-3";

    public IntegrationEvent<OrderCompletedEvent> create(
            OrderEntity order,
            BigDecimal totalAmount,
            String currency
    ) {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        OrderCompletedEvent payload =
                new OrderCompletedEvent(
                        order.getId(),
                        order.getRestaurantId().getId(),
                        totalAmount,
                        currency,
                        occurredAt
                );

        return new IntegrationEvent<>(
                eventId,
                EVENT_TYPE,
                EVENT_VERSION,
                occurredAt,
                PRODUCER,
                eventId,
                payload
        );
    }
}