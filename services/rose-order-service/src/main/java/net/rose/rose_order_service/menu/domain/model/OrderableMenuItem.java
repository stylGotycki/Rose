package net.rose.rose_order_service.menu.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderableMenuItem(
        UUID id,
        UUID restaurantId,
        String name,
        BigDecimal price,
        String currency
) {
}