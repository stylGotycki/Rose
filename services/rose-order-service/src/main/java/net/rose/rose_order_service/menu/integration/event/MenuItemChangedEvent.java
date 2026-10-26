package net.rose.rose_order_service.menu.integration.event;

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
}