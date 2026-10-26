package net.rose.rose_order_service.order.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderItemReadDto(
        UUID id,
        UUID menuItemId,
        String name,
        BigDecimal unitPrice,
        String currency,
        int quantity,
        String status,
        String note,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt
) {
}