package net.rose.rose_billing_service.core.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.messaging.order")
public record OrderMessagingProperties(
        String exchange,
        String completedRoutingKey,
        String completedQueue,
        String completedDeadLetterQueue
) {
}