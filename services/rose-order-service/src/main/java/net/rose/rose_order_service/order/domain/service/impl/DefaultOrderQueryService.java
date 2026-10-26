package net.rose.rose_order_service.order.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_order_service.core.exception.NotFoundException;
import net.rose.rose_order_service.core.persistence.id.OrderId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.order.domain.service.api.OrderQueryService;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import net.rose.rose_order_service.order.persistence.repository.api.OrderItemRepository;
import net.rose.rose_order_service.order.persistence.repository.api.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultOrderQueryService implements OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    public OrderEntity findById(
            RestaurantId restaurantId,
            UUID orderId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return orderRepository.findByIdAndRestaurantId(
                        orderId,
                        restaurantId
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order not found"
                ));
    }

    @Override
    public List<OrderEntity> findAll(RestaurantId restaurantId) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return orderRepository
                .findAllByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    @Override
    public List<OrderItemEntity> findItems(UUID orderId) {
        return orderItemRepository.findAllByOrderIdOrderByCreatedAtAsc(
                OrderId.of(orderId)
        );
    }
}