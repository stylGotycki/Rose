package net.rose.rose_order_service.order.domain.command;

import net.rose.rose_order_service.core.persistence.id.MenuItemId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;

import java.util.UUID;

public record OrderItemAddCommand(
        RestaurantId restaurantId,
        UUID orderId,
        MenuItemId menuItemId,
        int quantity,
        String note
) {
}