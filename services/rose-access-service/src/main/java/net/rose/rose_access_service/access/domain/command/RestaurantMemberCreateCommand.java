package net.rose.rose_access_service.access.domain.command;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;

public record RestaurantMemberCreateCommand(
        RestaurantId restaurantId,
        UserId userId,
        RestaurantMemberRole role
) {
}