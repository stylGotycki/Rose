package net.rose.rose_identity_service.core.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.messaging.access")
public record AccessMessagingProperties(
        String exchange,
        String accessGrantedRoutingKey,
        String accessGrantedQueue,
        String accessGrantedDeadLetterQueue
) {
}