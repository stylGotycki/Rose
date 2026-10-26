package net.rose.rose_organization_service.restaurant.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantCreateDto(

        @NotBlank
        @Size(max = 128)
        String name,

        @Size(max = 1024)
        String description,

        @Size(max = 32)
        String taxId
) {
}