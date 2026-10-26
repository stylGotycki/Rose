package net.rose.rose_identity_service.auth.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(
        @NotBlank
        String refreshToken
) {
}
