package net.rose.rose_access_service.access.domain.service.api;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantAccessQueryService {

    List<RestaurantMemberEntity> findActiveByRestaurantId(RestaurantId restaurantId);

    RestaurantMemberEntity findActiveById(UUID memberId);

    RestaurantMemberEntity findCurrentActiveMember(RestaurantId restaurantId);

    Optional<RestaurantMemberRole> findActiveRole(
            RestaurantId restaurantId,
            UserId userId
    );

    boolean hasActiveAccess(
            RestaurantId restaurantId,
            UserId userId
    );

    boolean hasActiveRole(
            RestaurantId restaurantId,
            UserId userId,
            RestaurantMemberRole role
    );
}