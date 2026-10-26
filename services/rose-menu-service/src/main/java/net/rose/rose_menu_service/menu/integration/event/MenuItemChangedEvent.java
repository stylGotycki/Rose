package net.rose.rose_menu_service.menu.integration.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MenuItemChangedEvent(
        UUID menuItemId,
        UUID restaurantId,
        String name,
        BigDecimal price,
        String currency,
        boolean orderable,
        Instant changedAt
) {

    public MenuItemChangedEvent {
        if (menuItemId == null) {
            throw new IllegalArgumentException(
                    "Menu item id cannot be null"
            );
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Menu item name cannot be blank"
            );
        }

        if (price == null) {
            throw new IllegalArgumentException(
                    "Menu item price cannot be null"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Menu item currency cannot be blank"
            );
        }

        if (changedAt == null) {
            throw new IllegalArgumentException(
                    "Changed at cannot be null"
            );
        }
    }
}