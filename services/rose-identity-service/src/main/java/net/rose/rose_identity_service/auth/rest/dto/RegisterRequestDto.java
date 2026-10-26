package net.rose.rose_identity_service.auth.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @NotBlank
        @Size(max = 64)
        String firstName,

        @NotBlank
        @Size(max = 64)
        String lastName,

        @NotBlank
        @Email
        @Size(max = 320)
        String email,


        @NotBlank
        @Size(min = 8, max = 128)
        String password
) {
}