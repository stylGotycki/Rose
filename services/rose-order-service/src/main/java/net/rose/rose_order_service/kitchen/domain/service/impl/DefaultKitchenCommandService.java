package net.rose.rose_order_service.kitchen.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_order_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_order_service.core.exception.NotFoundException;
import net.rose.rose_order_service.core.persistence.id.OrderId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.kitchen.domain.command.MarkOrderItemReadyCommand;
import net.rose.rose_order_service.kitchen.domain.command.StartPreparingOrderItemCommand;
import net.rose.rose_order_service.kitchen.domain.service.api.KitchenCommandService;
import net.rose.rose_order_service.order.domain.model.OrderItemStatus;
import net.rose.rose_order_service.order.domain.model.OrderStatus;
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
@Transactional
public class DefaultKitchenCommandService
        implements KitchenCommandService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    public OrderItemEntity startPreparing(
            StartPreparingOrderItemCommand command
    ) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderItemEntity item = findItem(
                command.restaurantId(),
                command.orderItemId()
        );

        if (item.getStatus() != OrderItemStatus.PENDING) {
            throw new BusinessRuleViolationException(
                    "Only pending order items can be started"
            );
        }

        OrderEntity order = findOrder(
                command.restaurantId(),
                item.getOrderId()
        );

        if (order.getStatus() != OrderStatus.SUBMITTED
                && order.getStatus() != OrderStatus.IN_PREPARATION) {
            throw new BusinessRuleViolationException(
                    "Order is not available for preparation"
            );
        }

        item.startPreparation();
        order.markInPreparation();

        orderRepository.save(order);

        return orderItemRepository.save(item);
    }

    @Override
    public OrderItemEntity markReady(
            MarkOrderItemReadyCommand command
    ) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderItemEntity item = findItem(
                command.restaurantId(),
                command.orderItemId()
        );

        if (item.getStatus() != OrderItemStatus.IN_PREPARATION) {
            throw new BusinessRuleViolationException(
                    "Only order items in preparation can be marked as ready"
            );
        }

        item.markReady();
        OrderItemEntity savedItem = orderItemRepository.save(item);

        updateOrderStatusIfReady(
                command.restaurantId(),
                item.getOrderId()
        );

        return savedItem;
    }

    private OrderItemEntity findItem(
            RestaurantId restaurantId,
            UUID orderItemId
    ) {
        OrderItemEntity item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException(
                        "Order item not found"
                ));

        findOrder(
                restaurantId,
                item.getOrderId()
        );

        return item;
    }

    private OrderEntity findOrder(
            RestaurantId restaurantId,
            OrderId orderId
    ) {
        return orderRepository.findByIdAndRestaurantId(
                        orderId.getId(),
                        restaurantId
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order not found"
                ));
    }

    private void updateOrderStatusIfReady(
            RestaurantId restaurantId,
            OrderId orderId
    ) {
        List<OrderItemEntity> items =
                orderItemRepository
                        .findAllByOrderIdOrderByCreatedAtAsc(orderId);

        boolean hasActiveItems = items.stream()
                .anyMatch(item ->
                        item.getStatus() != OrderItemStatus.CANCELLED
                );

        if (!hasActiveItems) {
            return;
        }

        boolean allReady = items.stream()
                .filter(item ->
                        item.getStatus() != OrderItemStatus.CANCELLED
                )
                .allMatch(item ->
                        item.getStatus() == OrderItemStatus.READY
                );

        if (!allReady) {
            return;
        }

        OrderEntity order = findOrder(
                restaurantId,
                orderId
        );

        order.markReady();
        orderRepository.save(order);
    }
}