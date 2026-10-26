package net.rose.rose_billing_service.billing.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_billing_service.billing.domain.command.BillPayCommand;
import net.rose.rose_billing_service.billing.domain.service.api.BillCommandService;
import net.rose.rose_billing_service.billing.domain.service.api.BillQueryService;
import net.rose.rose_billing_service.billing.persistence.entity.BillEntity;
import net.rose.rose_billing_service.billing.rest.controller.api.BillRestController;
import net.rose.rose_billing_service.billing.rest.dto.BillReadDto;
import net.rose.rose_billing_service.billing.rest.mapper.BillRestMapper;
import net.rose.rose_billing_service.core.persistence.id.RestaurantId;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DefaultBillRestController implements BillRestController {

    private final BillCommandService commandService;
    private final BillQueryService queryService;
    private final BillRestMapper mapper;


    @Override
    public BillReadDto getByOrderId(
            UUID restaurantId,
            UUID orderId
    ) {
        return mapper.toDto(
                queryService.findByOrderId(
                        RestaurantId.of(restaurantId),
                        orderId
                )
        );
    }

    @Override
    public BillReadDto getById(
            UUID restaurantId,
            UUID billId
    ) {
        return mapper.toDto(
                queryService.findById(
                        RestaurantId.of(restaurantId),
                        billId
                )
        );
    }

    @Override
    public List<BillReadDto> getAll(UUID restaurantId) {
        return queryService.findAll(
                        RestaurantId.of(restaurantId)
                )
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public BillReadDto pay(
            UUID restaurantId,
            UUID billId
    ) {
        BillEntity bill = commandService.pay(
                new BillPayCommand(
                        RestaurantId.of(restaurantId),
                        billId
                )
        );

        return mapper.toDto(bill);
    }
}