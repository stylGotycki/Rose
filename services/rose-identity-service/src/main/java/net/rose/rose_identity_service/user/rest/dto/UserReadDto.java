package net.rose.rose_identity_service.user.rest.dto;

import net.rose.rose_identity_service.user.domain.model.UserStatus;

import java.util.UUID;

public record UserReadDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        UserStatus status
) {
}
