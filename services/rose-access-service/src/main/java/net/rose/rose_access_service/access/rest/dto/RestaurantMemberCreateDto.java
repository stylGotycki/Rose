package net.rose.rose_access_service.access.rest.dto;

import jakarta.validation.constraints.NotNull;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;

import java.util.UUID;

public record RestaurantMemberCreateDto(
        @NotNull
        UUID userId,

        @NotNull
        RestaurantMemberRole role
) {
}