package net.rose.rose_order_service.order.integration.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCompletedEvent(
        UUID orderId,
        UUID restaurantId,
        BigDecimal totalAmount,
        String currency,
        Instant completedAt
) {

    public OrderCompletedEvent {
        if (orderId == null) {
            throw new IllegalArgumentException(
                    "Order id cannot be null"
            );
        }

        if (restaurantId == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (totalAmount == null
                || totalAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Order total amount must be greater than zero"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Order currency cannot be blank"
            );
        }

        if (completedAt == null) {
            throw new IllegalArgumentException(
                    "Completed at cannot be null"
            );
        }
    }
}