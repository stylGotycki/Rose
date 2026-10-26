package net.rose.rose_billing_service.billing.domain.command;

import net.rose.rose_billing_service.core.persistence.id.OrderId;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;

import java.math.BigDecimal;

public record CreateBillFromCompletedOrderCommand(
        OrderId orderId,
        RestaurantId restaurantId,
        BigDecimal totalAmount,
        String currency
) {
}