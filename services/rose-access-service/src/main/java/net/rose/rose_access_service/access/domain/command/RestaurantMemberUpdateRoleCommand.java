package net.rose.rose_access_service.access.domain.command;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;

public record RestaurantMemberUpdateRoleCommand(
        RestaurantMemberRole role
) {
}