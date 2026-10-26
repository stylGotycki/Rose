package net.rose.rose_billing_service.billing.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BillReadDto(
        UUID id,
        UUID orderId,
        UUID restaurantId,
        String status,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt,
        Instant updatedAt,
        Instant paidAt
) {
}