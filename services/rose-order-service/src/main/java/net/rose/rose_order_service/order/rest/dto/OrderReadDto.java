package net.rose.rose_order_service.order.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderReadDto(
        UUID id,
        UUID restaurantId,
        UUID createdByUserId,
        String status,
        String note,
        List<OrderItemReadDto> items,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        Instant updatedAt
) {
}