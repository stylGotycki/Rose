package net.rose.rose_access_service.access.rest.dto;

import jakarta.validation.constraints.NotNull;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;

public record RestaurantMemberUpdateDto(
        @NotNull
        RestaurantMemberRole role
) {
}