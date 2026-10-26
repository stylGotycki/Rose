package net.rose.rose_billing_service.billing.domain.command;

import net.rose.rose_billing_service.core.persistence.id.RestaurantId;

import java.util.UUID;

public record BillPayCommand(
        RestaurantId restaurantId,
        UUID billId
) {
}