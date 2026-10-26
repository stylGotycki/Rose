package net.rose.rose_identity_service.auth.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequestDto(

        @NotBlank
        @Email
        @Size(max = 320)
        String email

) {
}
