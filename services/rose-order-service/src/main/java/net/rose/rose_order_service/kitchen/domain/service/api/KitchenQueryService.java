package net.rose.rose_order_service.kitchen.domain.service.api;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;

import java.util.List;

public interface KitchenQueryService {

    List<OrderItemEntity> findActiveItems(
            RestaurantId restaurantId
    );
}