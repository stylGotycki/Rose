package net.rose.rose_order_service.order.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record OrderItemCreateDto(

        @NotNull
        UUID menuItemId,

        @Min(1)
        int quantity,

        @Size(max = 512)
        String note
) {
}