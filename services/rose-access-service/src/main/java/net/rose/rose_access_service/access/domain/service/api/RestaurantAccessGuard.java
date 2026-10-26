package net.rose.rose_access_service.access.domain.service.api;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;

public interface RestaurantAccessGuard {


    void requireAccess(RestaurantId restaurantId);


    void requireRole(RestaurantId restaurantId, RestaurantMemberRole... allowedRoles);

}