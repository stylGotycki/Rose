package net.rose.rose_order_service.order.domain.command;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;

import java.util.UUID;

public record OrderSubmitCommand(
        RestaurantId restaurantId,
        UUID orderId
) {
}