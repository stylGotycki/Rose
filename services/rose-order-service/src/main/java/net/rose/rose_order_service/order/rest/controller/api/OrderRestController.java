package net.rose.rose_order_service.order.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_order_service.order.rest.dto.OrderCreateDto;
import net.rose.rose_order_service.order.rest.dto.OrderItemCreateDto;
import net.rose.rose_order_service.order.rest.dto.OrderItemUpdateDto;
import net.rose.rose_order_service.order.rest.dto.OrderReadDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/restaurants/{restaurantId}/orders")
public interface OrderRestController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderReadDto create(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid OrderCreateDto dto
    );

    @PostMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    OrderReadDto addItem(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @RequestBody @Valid OrderItemCreateDto dto
    );

    @GetMapping("/{orderId}")
    OrderReadDto getById(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId
    );

    @GetMapping
    List<OrderReadDto> getAll(
            @PathVariable UUID restaurantId
    );

    @PutMapping("/{orderId}/items/{orderItemId}")
    OrderReadDto updateItem(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @PathVariable UUID orderItemId,
            @RequestBody @Valid OrderItemUpdateDto dto
    );

    @DeleteMapping("/{orderId}/items/{orderItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeItem(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @PathVariable UUID orderItemId
    );

    @PostMapping("/{orderId}/submit")
    OrderReadDto submit(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId
    );

    @PostMapping("/{orderId}/complete")
    OrderReadDto complete(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId
    );
}