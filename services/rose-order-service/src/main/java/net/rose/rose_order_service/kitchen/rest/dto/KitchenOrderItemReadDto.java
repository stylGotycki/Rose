package net.rose.rose_order_service.kitchen.rest.dto;

import java.time.Instant;
import java.util.UUID;

public record KitchenOrderItemReadDto(
        UUID id,
        UUID orderId,
        UUID menuItemId,
        String name,
        int quantity,
        String note,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}