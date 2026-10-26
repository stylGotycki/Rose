package net.rose.rose_identity_service.user.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(

        @NotBlank
        @Size(max = 64)
        String firstName,


        @NotBlank
        @Size(max = 64)
        String lastName

) {
}
