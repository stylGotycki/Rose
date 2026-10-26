package net.rose.rose_order_service.order.rest.dto;

import jakarta.validation.constraints.Size;

public record OrderCreateDto(

        @Size(max = 512)
        String note
) {
}