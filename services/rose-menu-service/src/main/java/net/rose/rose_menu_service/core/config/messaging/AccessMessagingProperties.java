package net.rose.rose_menu_service.core.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.messaging.access")
public record AccessMessagingProperties(
        String exchange,
        String memberChangedRoutingKey,
        String memberChangedQueue,
        String memberChangedDeadLetterQueue
) {
}