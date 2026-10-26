package net.rose.rose_menu_service.menu.integration.event;

import net.rose.rose_menu_service.core.integration.event.IntegrationEvent;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class MenuItemChangedEventFactory {

    private static final String EVENT_TYPE =
            "menu.item-changed";

    private static final int EVENT_VERSION = 1;

    private static final String PRODUCER =
            "rose-layer-2";

    public IntegrationEvent<MenuItemChangedEvent> create(
            MenuItemEntity item,
            MenuCategoryEntity category,
            MenuEntity menu
    ) {
        Instant occurredAt = Instant.now();
        UUID eventId = UUID.randomUUID();

        boolean orderable =
                item.getStatus() == MenuItemStatus.ACTIVE
                        && Boolean.TRUE.equals(item.getAvailable())
                        && category.getStatus()
                        == MenuCategoryStatus.ACTIVE
                        && menu.getStatus() == MenuStatus.ACTIVE;

        MenuItemChangedEvent payload =
                new MenuItemChangedEvent(
                        item.getId(),
                        menu.getRestaurantId().getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getCurrency(),
                        orderable,
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