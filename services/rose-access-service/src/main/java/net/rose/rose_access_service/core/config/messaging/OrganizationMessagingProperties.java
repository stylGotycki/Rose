package net.rose.rose_access_service.core.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.messaging.organization")
public record OrganizationMessagingProperties(
        String exchange,
        String restaurantCreatedRoutingKey,
        String restaurantCreatedQueue,
        String restaurantCreatedDeadLetterQueue
) {
}