package net.rose.rose_order_service.order.domain.service.api;

import net.rose.rose_order_service.order.domain.command.*;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;

public interface OrderCommandService {

    OrderEntity create(OrderCreateCommand command);

    OrderItemEntity addItem(OrderItemAddCommand command);

    OrderItemEntity updateItem(OrderItemUpdateCommand command);

    void removeItem(OrderItemRemoveCommand command);

    OrderEntity submit(OrderSubmitCommand command);

    OrderEntity complete(CompleteOrderCommand command);
}