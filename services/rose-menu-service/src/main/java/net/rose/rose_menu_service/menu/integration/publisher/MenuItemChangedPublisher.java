package net.rose.rose_menu_service.menu.integration.publisher;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.core.config.messaging.MenuMessagingProperties;
import net.rose.rose_menu_service.core.integration.event.IntegrationEvent;
import net.rose.rose_menu_service.core.integration.outbox.domain.service.api.OutboxEventWriter;
import net.rose.rose_menu_service.menu.integration.event.MenuItemChangedEvent;
import net.rose.rose_menu_service.menu.integration.event.MenuItemChangedEventFactory;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuItemChangedPublisher {

    private final MenuItemChangedEventFactory eventFactory;
    private final OutboxEventWriter outboxEventWriter;
    private final MenuMessagingProperties messagingProperties;

    public void publish(
            MenuItemEntity item,
            MenuCategoryEntity category,
            MenuEntity menu
    ) {
        IntegrationEvent<MenuItemChangedEvent> event =
                eventFactory.create(
                        item,
                        category,
                        menu
                );

        outboxEventWriter.write(
                messagingProperties.exchange(),
                messagingProperties.itemChangedRoutingKey(),
                event
        );
    }
}