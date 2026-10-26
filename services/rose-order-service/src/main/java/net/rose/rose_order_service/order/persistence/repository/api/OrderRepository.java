package net.rose.rose_order_service.order.persistence.repository.api;

import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.order.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository
        extends JpaRepository<OrderEntity, UUID> {

    Optional<OrderEntity> findByIdAndRestaurantId(
            UUID id,
            RestaurantId restaurantId
    );

    List<OrderEntity> findAllByRestaurantIdOrderByCreatedAtDesc(
            RestaurantId restaurantId
    );
}