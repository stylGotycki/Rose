package net.rose.rose_identity_service.auth.rest.dto;

public record TokenPairDto(
        String accessToken,
        String refreshToken
) {
}
