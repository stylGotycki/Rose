package net.rose.rose_order_service.order.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record OrderItemUpdateDto(

        @Min(1)
        int quantity,

        @Size(max = 512)
        String note
) {
}