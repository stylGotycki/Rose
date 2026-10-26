package net.rose.rose_order_service.kitchen.domain.service.api;

import net.rose.rose_order_service.kitchen.domain.command.MarkOrderItemReadyCommand;
import net.rose.rose_order_service.kitchen.domain.command.StartPreparingOrderItemCommand;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;

public interface KitchenCommandService {

    OrderItemEntity startPreparing(
            StartPreparingOrderItemCommand command
    );

    OrderItemEntity markReady(
            MarkOrderItemReadyCommand command
    );
}