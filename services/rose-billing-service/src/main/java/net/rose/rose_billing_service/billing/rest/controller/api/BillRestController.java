package net.rose.rose_billing_service.billing.rest.controller.api;

import net.rose.rose_billing_service.billing.rest.dto.BillReadDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/restaurants/{restaurantId}")
public interface BillRestController {


    @GetMapping("/orders/{orderId}/bill")
    BillReadDto getByOrderId(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId
    );

    @GetMapping("/bills/{billId}")
    BillReadDto getById(
            @PathVariable UUID restaurantId,
            @PathVariable UUID billId
    );

    @GetMapping("/bills")
    List<BillReadDto> getAll(
            @PathVariable UUID restaurantId
    );

    @PostMapping("/bills/{billId}/pay")
    BillReadDto pay(
            @PathVariable UUID restaurantId,
            @PathVariable UUID billId
    );
}