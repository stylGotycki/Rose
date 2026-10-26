package net.rose.rose_order_service.kitchen.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.kitchen.domain.service.api.KitchenQueryService;
import net.rose.rose_order_service.order.domain.model.OrderItemStatus;
import net.rose.rose_order_service.order.domain.model.OrderStatus;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import net.rose.rose_order_service.order.persistence.repository.api.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultKitchenQueryService
        implements KitchenQueryService {

    private final OrderItemRepository orderItemRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    public List<OrderItemEntity> findActiveItems(
            RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return orderItemRepository.findKitchenItems(
                restaurantId.getId(),
                List.of(
                        OrderStatus.SUBMITTED,
                        OrderStatus.IN_PREPARATION
                ),
                List.of(
                        OrderItemStatus.PENDING,
                        OrderItemStatus.IN_PREPARATION
                )
        );
    }
}