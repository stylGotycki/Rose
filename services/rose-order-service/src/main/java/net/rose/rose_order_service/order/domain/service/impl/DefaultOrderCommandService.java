package net.rose.rose_order_service.order.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_order_service.core.config.security.CurrentUserProvider;
import net.rose.rose_order_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_order_service.core.exception.NotFoundException;
import net.rose.rose_order_service.core.persistence.id.OrderId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.menu.domain.model.OrderableMenuItem;
import net.rose.rose_order_service.menu.domain.service.api.MenuItemCatalog;
import net.rose.rose_order_service.order.domain.command.*;
import net.rose.rose_order_service.order.domain.model.OrderItemStatus;
import net.rose.rose_order_service.order.domain.model.OrderStatus;
import net.rose.rose_order_service.order.domain.service.api.OrderCommandService;
import net.rose.rose_order_service.order.integration.publisher.OrderCompletedPublisher;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import net.rose.rose_order_service.order.persistence.repository.api.OrderItemRepository;
import net.rose.rose_order_service.order.persistence.repository.api.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultOrderCommandService implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;
    private final CurrentUserProvider currentUserProvider;
    private final MenuItemCatalog menuItemCatalog;
    private final OrderCompletedPublisher orderCompletedPublisher;

    @Override
    public OrderEntity create(OrderCreateCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = OrderEntity.create(
                command.restaurantId(),
                currentUserProvider.getCurrentUserId(),
                command.note()
        );

        return orderRepository.save(order);
    }

    @Override
    public OrderItemEntity addItem(OrderItemAddCommand command) {
        if (command.quantity() <= 0) {
            throw new BusinessRuleViolationException(
                    "Order item quantity must be greater than zero"
            );
        }

        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = orderRepository
                .findByIdAndRestaurantId(
                        command.orderId(),
                        command.restaurantId()
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order not found"
                ));

        if (!order.isDraft()) {
            throw new BusinessRuleViolationException(
                    "Items can only be added to a draft order"
            );
        }

        OrderableMenuItem menuItem =
                menuItemCatalog.getOrderableItem(
                        command.restaurantId(),
                        command.menuItemId()
                );

        OrderItemEntity orderItem = OrderItemEntity.create(
                OrderId.of(order.getId()),
                command.menuItemId(),
                menuItem.name(),
                menuItem.price(),
                menuItem.currency(),
                command.quantity(),
                command.note()
        );

        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItemEntity updateItem(OrderItemUpdateCommand command) {
        if (command.quantity() <= 0) {
            throw new BusinessRuleViolationException(
                    "Order item quantity must be greater than zero"
            );
        }

        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = findDraftOrder(
                command.restaurantId(),
                command.orderId()
        );

        OrderItemEntity item = orderItemRepository
                .findByIdAndOrderId(
                        command.orderItemId(),
                        OrderId.of(order.getId())
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order item not found"
                ));

        item.update(
                command.quantity(),
                command.note()
        );

        return orderItemRepository.save(item);
    }

    @Override
    public void removeItem(OrderItemRemoveCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = findDraftOrder(
                command.restaurantId(),
                command.orderId()
        );

        OrderItemEntity item = orderItemRepository
                .findByIdAndOrderId(
                        command.orderItemId(),
                        OrderId.of(order.getId())
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order item not found"
                ));

        orderItemRepository.delete(item);
    }

    @Override
    public OrderEntity submit(OrderSubmitCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = findDraftOrder(
                command.restaurantId(),
                command.orderId()
        );

        boolean hasItems = orderItemRepository.existsByOrderId(
                OrderId.of(order.getId())
        );

        if (!hasItems) {
            throw new BusinessRuleViolationException(
                    "Empty order cannot be submitted"
            );
        }

        order.submit();

        return orderRepository.save(order);
    }

    private OrderEntity findDraftOrder(
            RestaurantId restaurantId,
            UUID orderId
    ) {
        OrderEntity order = orderRepository
                .findByIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> new NotFoundException(
                        "Order not found"
                ));

        if (!order.isDraft()) {
            throw new BusinessRuleViolationException(
                    "Only draft orders can be modified"
            );
        }

        return order;
    }

    @Override
    public OrderEntity complete(CompleteOrderCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        OrderEntity order = orderRepository
                .findByIdAndRestaurantId(
                        command.orderId(),
                        command.restaurantId()
                )
                .orElseThrow(() -> new NotFoundException(
                        "Order not found"
                ));

        if (order.getStatus() != OrderStatus.READY) {
            throw new BusinessRuleViolationException(
                    "Only ready orders can be completed"
            );
        }

        List<OrderItemEntity> items =
                orderItemRepository.findAllByOrderIdOrderByCreatedAtAsc(
                        OrderId.of(order.getId())
                );

        List<OrderItemEntity> billableItems = items.stream()
                .filter(item ->
                        item.getStatus() != OrderItemStatus.CANCELLED
                )
                .toList();

        if (billableItems.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Completed order must contain billable items"
            );
        }

        Set<String> currencies = billableItems.stream()
                .map(OrderItemEntity::getCurrency)
                .collect(Collectors.toSet());

        if (currencies.size() != 1) {
            throw new BusinessRuleViolationException(
                    "All order items must use the same currency"
            );
        }

        String currency = currencies.iterator().next();

        BigDecimal totalAmount = billableItems.stream()
                .map(item ->
                        item.getUnitPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.signum() <= 0) {
            throw new BusinessRuleViolationException(
                    "Order total amount must be greater than zero"
            );
        }

        billableItems.stream()
                .filter(item ->
                        item.getStatus() == OrderItemStatus.READY
                )
                .forEach(OrderItemEntity::markServed);

        orderItemRepository.saveAll(billableItems);

        order.complete();

        OrderEntity savedOrder =
                orderRepository.save(order);

        orderCompletedPublisher.publish(
                savedOrder,
                totalAmount,
                currency
        );

        return savedOrder;
    }

}