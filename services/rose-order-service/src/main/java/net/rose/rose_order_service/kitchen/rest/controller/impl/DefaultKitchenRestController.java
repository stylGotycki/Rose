package net.rose.rose_order_service.kitchen.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.kitchen.domain.command.MarkOrderItemReadyCommand;
import net.rose.rose_order_service.kitchen.domain.command.StartPreparingOrderItemCommand;
import net.rose.rose_order_service.kitchen.domain.service.api.KitchenCommandService;
import net.rose.rose_order_service.kitchen.domain.service.api.KitchenQueryService;
import net.rose.rose_order_service.kitchen.rest.controller.api.KitchenRestController;
import net.rose.rose_order_service.kitchen.rest.dto.KitchenOrderItemReadDto;
import net.rose.rose_order_service.kitchen.rest.mapper.KitchenRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DefaultKitchenRestController
        implements KitchenRestController {

    private final KitchenQueryService queryService;
    private final KitchenCommandService commandService;
    private final KitchenRestMapper mapper;

    @Override
    public List<KitchenOrderItemReadDto> getActiveItems(
            UUID restaurantId
    ) {
        return queryService.findActiveItems(
                        RestaurantId.of(restaurantId)
                )
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public KitchenOrderItemReadDto startPreparing(
            UUID restaurantId,
            UUID orderItemId
    ) {
        return mapper.toDto(
                commandService.startPreparing(
                        new StartPreparingOrderItemCommand(
                                RestaurantId.of(restaurantId),
                                orderItemId
                        )
                )
        );
    }

    @Override
    public KitchenOrderItemReadDto markReady(
            UUID restaurantId,
            UUID orderItemId
    ) {
        return mapper.toDto(
                commandService.markReady(
                        new MarkOrderItemReadyCommand(
                                RestaurantId.of(restaurantId),
                                orderItemId
                        )
                )
        );
    }
}