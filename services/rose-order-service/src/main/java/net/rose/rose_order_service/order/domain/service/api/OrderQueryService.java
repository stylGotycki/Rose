package net.rose.rose_order_service.order.domain.service.api;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;

import java.util.List;
import java.util.UUID;

public interface OrderQueryService {

    OrderEntity findById(
            RestaurantId restaurantId,
            UUID orderId
    );

    List<OrderEntity> findAll(RestaurantId restaurantId);

    List<OrderItemEntity> findItems(UUID orderId);
}