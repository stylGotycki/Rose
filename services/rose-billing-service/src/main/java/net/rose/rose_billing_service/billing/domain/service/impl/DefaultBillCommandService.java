package net.rose.rose_billing_service.billing.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_billing_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_billing_service.billing.domain.command.BillCreateCommand;
import net.rose.rose_billing_service.billing.domain.command.BillPayCommand;
import net.rose.rose_billing_service.billing.domain.command.CreateBillFromCompletedOrderCommand;
import net.rose.rose_billing_service.billing.domain.model.BillStatus;
import net.rose.rose_billing_service.billing.domain.service.api.BillCommandService;
import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.billing.persistence.repository.api.BillRepository;
import net.rose.rose_billing_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_billing_service.core.exception.NotFoundException;
import net.rose.rose_billing_service.core.persistence.id.OrderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class DefaultBillCommandService
        implements BillCommandService {

    private final BillRepository billRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    public BillEntity create(BillCreateCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        return billRepository.findByOrderId(
                        OrderId.of(command.orderId())
                )
                .orElseThrow(() ->
                        new BusinessRuleViolationException(
                                "Bill has not been generated yet"
                        )
                );
    }


    @Override
    public BillEntity pay(BillPayCommand command) {
        restaurantAccessGuard.requireActiveAccess(
                command.restaurantId()
        );

        BillEntity bill = billRepository
                .findByIdAndRestaurantId(
                        command.billId(),
                        command.restaurantId()
                )
                .orElseThrow(() -> new NotFoundException(
                        "Bill not found"
                ));

        if (bill.getStatus() != BillStatus.OPEN) {
            throw new BusinessRuleViolationException(
                    "Only open bills can be paid"
            );
        }

        bill.pay();

        return billRepository.save(bill);
    }


    @Override
    public BillEntity createFromCompletedOrder(
            CreateBillFromCompletedOrderCommand command
    ) {
        return billRepository.findByOrderId(command.orderId())
                .orElseGet(() -> {
                    validateCompletedOrderData(command);

                    BillEntity bill = BillEntity.create(
                            command.orderId(),
                            command.restaurantId(),
                            command.totalAmount(),
                            command.currency()
                    );

                    return billRepository.save(bill);
                });
    }

    private void validateCompletedOrderData(
            CreateBillFromCompletedOrderCommand command
    ) {
        if (command.orderId() == null) {
            throw new IllegalArgumentException(
                    "Order id cannot be null"
            );
        }

        if (command.restaurantId() == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (command.totalAmount() == null
                || command.totalAmount().signum() <= 0) {
            throw new BusinessRuleViolationException(
                    "Bill total amount must be greater than zero"
            );
        }

        if (command.currency() == null
                || command.currency().isBlank()) {
            throw new BusinessRuleViolationException(
                    "Bill currency cannot be blank"
            );
        }
    }
}