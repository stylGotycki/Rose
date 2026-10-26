package net.rose.rose_order_service.order.rest.mapper;

import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import net.rose.rose_order_service.order.rest.dto.OrderItemReadDto;
import net.rose.rose_order_service.order.rest.dto.OrderReadDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderRestMapper {

    public OrderReadDto toDto(
            OrderEntity order,
            List<OrderItemEntity> items
    ) {
        List<OrderItemReadDto> itemDtos = items.stream()
                .map(this::toDto)
                .toList();

        BigDecimal totalAmount = itemDtos.stream()
                .map(OrderItemReadDto::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = itemDtos.stream()
                .map(OrderItemReadDto::currency)
                .findFirst()
                .orElse(null);

        return new OrderReadDto(
                order.getId(),
                order.getRestaurantId().getId(),
                order.getCreatedByUserId().getId(),
                order.getStatus().name(),
                order.getNote(),
                itemDtos,
                totalAmount,
                currency,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public OrderItemReadDto toDto(OrderItemEntity item) {
        BigDecimal totalPrice = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new OrderItemReadDto(
                item.getId(),
                item.getMenuItemId().getId(),
                item.getName(),
                item.getUnitPrice(),
                item.getCurrency(),
                item.getQuantity(),
                item.getStatus().name(),
                item.getNote(),
                totalPrice,
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}