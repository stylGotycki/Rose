package net.rose.rose_identity_service.auth.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequestDto(

        @NotBlank
        @Size(max = 128)
        String oldPassword,

        @NotBlank
        @Size(min = 8, max = 128)
        String newPassword
) {
}