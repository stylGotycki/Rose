package net.rose.rose_billing_service.order.integration.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCompletedEvent(
        UUID orderId,
        UUID restaurantId,
        BigDecimal totalAmount,
        String currency
) {
}