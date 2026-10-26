package net.rose.rose_order_service.kitchen.rest.mapper;

import net.rose.rose_order_service.kitchen.rest.dto.KitchenOrderItemReadDto;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class KitchenRestMapper {

    public KitchenOrderItemReadDto toDto(
            OrderItemEntity item
    ) {
        return new KitchenOrderItemReadDto(
                item.getId(),
                item.getOrderId().getId(),
                item.getMenuItemId().getId(),
                item.getName(),
                item.getQuantity(),
                item.getNote(),
                item.getStatus().name(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}