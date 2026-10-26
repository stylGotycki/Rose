package net.rose.rose_order_service.order.integration.publisher;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.core.config.messaging.OrderMessagingProperties;
import net.rose.rose_order_service.core.integration.event.IntegrationEvent;
import net.rose.rose_order_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import net.rose.rose_order_service.order.integration.event.OrderCompletedEvent;
import net.rose.rose_order_service.order.integration.event.OrderCompletedEventFactory;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderCompletedPublisher {

    private final OrderCompletedEventFactory eventFactory;
    private final OutboxEventWriter outboxEventWriter;
    private final OrderMessagingProperties messagingProperties;

    public void publish(
            OrderEntity order,
            BigDecimal totalAmount,
            String currency
    ) {
        IntegrationEvent<OrderCompletedEvent> event =
                eventFactory.create(
                        order,
                        totalAmount,
                        currency
                );

        outboxEventWriter.write(
                messagingProperties.exchange(),
                messagingProperties.completedRoutingKey(),
                event
        );
    }
}