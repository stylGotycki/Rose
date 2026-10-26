package net.rose.rose_billing_service.billing.domain.service.api;

import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;

import java.util.List;
import java.util.UUID;

public interface BillQueryService {

    BillEntity findById(
            RestaurantId restaurantId,
            UUID billId
    );

    BillEntity findByOrderId(
            RestaurantId restaurantId,
            UUID orderId
    );

    List<BillEntity> findAll(
            RestaurantId restaurantId
    );
}