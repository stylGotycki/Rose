package net.rose.rose_access_service.access.rest.dto;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberStatus;

import java.time.Instant;
import java.util.UUID;

public record RestaurantMemberReadDto(
        UUID id,
        UUID restaurantId,
        UUID userId,
        RestaurantMemberRole role,
        RestaurantMemberStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}