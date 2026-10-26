package net.rose.rose_menu_service.core.config.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rose.messaging.menu")
public record MenuMessagingProperties(
        String exchange,
        String itemChangedRoutingKey
) {
}