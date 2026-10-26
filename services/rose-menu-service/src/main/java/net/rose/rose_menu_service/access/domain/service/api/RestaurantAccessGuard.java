package net.rose.rose_menu_service.access.domain.service.api;

import net.rose.rose_menu_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;

public interface RestaurantAccessGuard {

    RestaurantMemberRole requireActiveAccess(RestaurantId restaurantId);

    void requireAnyRole(
            RestaurantId restaurantId,
            RestaurantMemberRole... allowedRoles
    );
}