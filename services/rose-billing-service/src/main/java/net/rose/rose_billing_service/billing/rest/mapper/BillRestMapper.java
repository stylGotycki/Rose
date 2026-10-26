package net.rose.rose_billing_service.billing.rest.mapper;

import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.billing.rest.dto.BillReadDto;
import org.springframework.stereotype.Component;

@Component
public class BillRestMapper {

    public BillReadDto toDto(BillEntity bill) {
        return new BillReadDto(
                bill.getId(),
                bill.getOrderId().getId(),
                bill.getRestaurantId().getId(),
                bill.getStatus().name(),
                bill.getTotalAmount(),
                bill.getCurrency(),
                bill.getCreatedAt(),
                bill.getUpdatedAt(),
                bill.getPaidAt()
        );
    }
}