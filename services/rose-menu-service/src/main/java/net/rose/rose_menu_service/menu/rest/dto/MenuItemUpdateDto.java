package net.rose.rose_menu_service.menu.rest.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record MenuItemUpdateDto(
        @NotBlank
        @Size(max = 128)
        String name,

        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal price,

        @NotBlank
        @Size(min = 3, max = 3)
        String currency,

        @NotNull
        Boolean available
) {
}