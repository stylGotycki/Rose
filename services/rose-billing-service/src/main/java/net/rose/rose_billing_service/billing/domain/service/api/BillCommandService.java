package net.rose.rose_billing_service.billing.domain.service.api;

import net.rose.rose_billing_service.billing.domain.command.BillCreateCommand;
import net.rose.rose_billing_service.billing.domain.command.BillPayCommand;
import net.rose.rose_billing_service.billing.domain.command.CreateBillFromCompletedOrderCommand;
import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;

public interface BillCommandService {


    BillEntity create(BillCreateCommand command);

    BillEntity pay(BillPayCommand command);

    BillEntity createFromCompletedOrder(
            CreateBillFromCompletedOrderCommand command
    );
}