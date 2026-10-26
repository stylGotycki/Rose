package net.rose.rose_billing_service.billing.persistence.repository.api;

import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.core.persistence.id.OrderId;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository
        extends JpaRepository<BillEntity, UUID> {

    boolean existsByOrderId(OrderId orderId);

    Optional<BillEntity> findByOrderId(OrderId orderId);

    Optional<BillEntity> findByIdAndRestaurantId(
            UUID id,
            RestaurantId restaurantId
    );

    List<BillEntity> findAllByRestaurantIdOrderByCreatedAtDesc(
            RestaurantId restaurantId
    );
}