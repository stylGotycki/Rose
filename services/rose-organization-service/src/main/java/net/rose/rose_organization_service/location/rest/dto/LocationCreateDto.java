package net.rose.rose_organization_service.location.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationCreateDto(

        @NotBlank
        @Size(max = 128)
        String name,

        @NotBlank
        @Size(max = 128)
        String city,

        @NotBlank
        @Size(max = 128)
        String street,

        @NotBlank
        @Size(max = 32)
        String buildingNumber,

        @NotBlank
        @Size(max = 16)
        String postalCode
) {
}