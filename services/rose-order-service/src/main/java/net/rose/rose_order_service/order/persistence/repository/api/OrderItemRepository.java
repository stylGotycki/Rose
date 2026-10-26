package net.rose.rose_order_service.order.persistence.repository.api;

import net.rose.rose_order_service.core.persistence.id.OrderId;
import net.rose.rose_order_service.order.domain.model.OrderItemStatus;
import net.rose.rose_order_service.order.domain.model.OrderStatus;
import net.rose.rose_order_service.order.persistence.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository
        extends JpaRepository<OrderItemEntity, UUID> {

    List<OrderItemEntity> findAllByOrderIdOrderByCreatedAtAsc(
            OrderId orderId
    );

    Optional<OrderItemEntity> findByIdAndOrderId(
            UUID id,
            OrderId orderId
    );

    boolean existsByOrderId(OrderId orderId);

    @Query("""
            select item
            from OrderItemEntity item
            join OrderEntity orders
                on orders.id = item.orderId.id
            where orders.restaurantId.id = :restaurantId
              and orders.status in :orderStatuses
              and item.status in :itemStatuses
            order by item.createdAt asc
            """)
    List<OrderItemEntity> findKitchenItems(
            @Param("restaurantId") UUID restaurantId,
            @Param("orderStatuses") Collection<OrderStatus> orderStatuses,
            @Param("itemStatuses") Collection<OrderItemStatus> itemStatuses
    );
}