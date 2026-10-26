package net.rose.rose_access_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessQueryService;
import net.rose.rose_access_service.core.config.security.CurrentUserProvider;
import net.rose.rose_access_service.core.exception.ForbiddenException;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
class DefaultRestaurantAccessGuard implements RestaurantAccessGuard {

    private final CurrentUserProvider currentUserProvider;


    private final RestaurantAccessQueryService queryService;


    @Override
    public void requireAccess(RestaurantId restaurantId) {
        UserId currentUserId = currentUserProvider.getCurrentUserId();

        if (!queryService.hasActiveAccess(restaurantId, currentUserId)) {
            throw new ForbiddenException("No access to this restaurant");
        }
    }


    @Override
    public void requireRole(RestaurantId restaurantId, RestaurantMemberRole... allowedRoles) {
        UserId currentUserId = currentUserProvider.getCurrentUserId();

        RestaurantMemberRole currentRole = queryService.findActiveRole(restaurantId, currentUserId)
                .orElseThrow(() -> new ForbiddenException("No access to this restaurant"));

        boolean allowed = Arrays.asList(allowedRoles).contains(currentRole);

        if (!allowed) {
            throw new ForbiddenException("Insufficient restaurant permissions");
        }
    }
}