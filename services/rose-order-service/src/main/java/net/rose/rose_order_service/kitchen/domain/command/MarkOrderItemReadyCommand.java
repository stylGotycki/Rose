package net.rose.rose_order_service.kitchen.domain.command;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;

import java.util.UUID;

public record MarkOrderItemReadyCommand(
        RestaurantId restaurantId,
        UUID orderItemId
) {
}