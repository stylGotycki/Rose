package net.rose.rose_order_service.order.domain.command;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;

import java.util.UUID;

public record OrderItemUpdateCommand(
        RestaurantId restaurantId,
        UUID orderId,
        UUID orderItemId,
        int quantity,
        String note
) {
}