package net.rose.rose_order_service.order.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.core.persistence.id.MenuItemId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.order.domain.command.*;
import net.rose.rose_order_service.order.domain.service.api.OrderCommandService;
import net.rose.rose_order_service.order.domain.service.api.OrderQueryService;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.rest.controller.api.OrderRestController;
import net.rose.rose_order_service.order.rest.dto.OrderCreateDto;
import net.rose.rose_order_service.order.rest.dto.OrderItemCreateDto;
import net.rose.rose_order_service.order.rest.dto.OrderItemUpdateDto;
import net.rose.rose_order_service.order.rest.dto.OrderReadDto;
import net.rose.rose_order_service.order.rest.mapper.OrderRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DefaultOrderRestController
        implements OrderRestController {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;
    private final OrderRestMapper mapper;

    @Override
    public OrderReadDto create(
            UUID restaurantId,
            OrderCreateDto dto
    ) {
        OrderEntity order = commandService.create(
                new OrderCreateCommand(
                        RestaurantId.of(restaurantId),
                        dto.note()
                )
        );

        return mapper.toDto(order, List.of());
    }

    @Override
    public OrderReadDto addItem(
            UUID restaurantId,
            UUID orderId,
            OrderItemCreateDto dto
    ) {
        commandService.addItem(
                new OrderItemAddCommand(
                        RestaurantId.of(restaurantId),
                        orderId,
                        MenuItemId.of(dto.menuItemId()),
                        dto.quantity(),
                        dto.note()
                )
        );

        OrderEntity order = queryService.findById(
                RestaurantId.of(restaurantId),
                orderId
        );

        return mapper.toDto(
                order,
                queryService.findItems(orderId)
        );
    }

    @Override
    public OrderReadDto getById(
            UUID restaurantId,
            UUID orderId
    ) {
        OrderEntity order = queryService.findById(
                RestaurantId.of(restaurantId),
                orderId
        );

        return mapper.toDto(
                order,
                queryService.findItems(orderId)
        );
    }

    @Override
    public List<OrderReadDto> getAll(UUID restaurantId) {
        RestaurantId wrappedRestaurantId =
                RestaurantId.of(restaurantId);

        return queryService.findAll(wrappedRestaurantId)
                .stream()
                .map(order -> mapper.toDto(
                        order,
                        queryService.findItems(order.getId())
                ))
                .toList();
    }

    @Override
    public OrderReadDto updateItem(
            UUID restaurantId,
            UUID orderId,
            UUID orderItemId,
            OrderItemUpdateDto dto
    ) {
        commandService.updateItem(
                new OrderItemUpdateCommand(
                        RestaurantId.of(restaurantId),
                        orderId,
                        orderItemId,
                        dto.quantity(),
                        dto.note()
                )
        );

        return getById(restaurantId, orderId);
    }

    @Override
    public void removeItem(
            UUID restaurantId,
            UUID orderId,
            UUID orderItemId
    ) {
        commandService.removeItem(
                new OrderItemRemoveCommand(
                        RestaurantId.of(restaurantId),
                        orderId,
                        orderItemId
                )
        );
    }

    @Override
    public OrderReadDto submit(
            UUID restaurantId,
            UUID orderId
    ) {
        OrderEntity order = commandService.submit(
                new OrderSubmitCommand(
                        RestaurantId.of(restaurantId),
                        orderId
                )
        );

        return mapper.toDto(
                order,
                queryService.findItems(orderId)
        );
    }

    @Override
    public OrderReadDto complete(
            UUID restaurantId,
            UUID orderId
    ) {
        OrderEntity order = commandService.complete(
                new CompleteOrderCommand(
                        RestaurantId.of(restaurantId),
                        orderId
                )
        );

        return mapper.toDto(
                order,
                queryService.findItems(orderId)
        );
    }
}