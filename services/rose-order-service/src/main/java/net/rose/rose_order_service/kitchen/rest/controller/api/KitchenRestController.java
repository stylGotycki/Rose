package net.rose.rose_order_service.kitchen.rest.controller.api;

import net.rose.rose_order_service.kitchen.rest.dto.KitchenOrderItemReadDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/restaurants/{restaurantId}/kitchen/items")
public interface KitchenRestController {

    @GetMapping
    List<KitchenOrderItemReadDto> getActiveItems(
            @PathVariable UUID restaurantId
    );

    @PostMapping("/{orderItemId}/start")
    KitchenOrderItemReadDto startPreparing(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderItemId
    );

    @PostMapping("/{orderItemId}/ready")
    KitchenOrderItemReadDto markReady(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderItemId
    );
}