package net.rose.rose_billing_service.billing.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_billing_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_billing_service.billing.domain.service.api.BillQueryService;
import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.billing.persistence.repository.api.BillRepository;
import net.rose.rose_billing_service.core.exception.NotFoundException;
import net.rose.rose_billing_service.core.persistence.id.OrderId;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultBillQueryService
        implements BillQueryService {

    private final BillRepository billRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    public BillEntity findById(
            RestaurantId restaurantId,
            UUID billId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return billRepository
                .findByIdAndRestaurantId(
                        billId,
                        restaurantId
                )
                .orElseThrow(() -> new NotFoundException(
                        "Bill not found"
                ));
    }

    @Override
    public BillEntity findByOrderId(
            RestaurantId restaurantId,
            UUID orderId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        BillEntity bill = billRepository
                .findByOrderId(OrderId.of(orderId))
                .orElseThrow(() -> new NotFoundException(
                        "Bill not found"
                ));

        if (!bill.getRestaurantId().equals(restaurantId)) {
            throw new NotFoundException(
                    "Bill not found"
            );
        }

        return bill;
    }

    @Override
    public List<BillEntity> findAll(
            RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return billRepository
                .findAllByRestaurantIdOrderByCreatedAtDesc(
                        restaurantId
                );
    }
}