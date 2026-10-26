package net.rose.rose_access_service.access.persistence.repository.api;

import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberStatus;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantMemberRepository extends JpaRepository<RestaurantMemberEntity, UUID> {

    List<RestaurantMemberEntity> findAllByRestaurantIdAndStatusOrderByCreatedAtAsc(
            RestaurantId restaurantId,
            RestaurantMemberStatus status
    );


    Optional<RestaurantMemberEntity> findByRestaurantIdAndUserIdAndStatus(
            RestaurantId restaurantId,
            UserId userId,
            RestaurantMemberStatus status
    );


    Optional<RestaurantMemberEntity> findByRestaurantIdAndUserId(
            RestaurantId restaurantId,
            UserId userId
    );


    Optional<RestaurantMemberEntity> findByIdAndStatus(
            UUID id,
            RestaurantMemberStatus status
    );


    boolean existsByRestaurantIdAndUserIdAndStatus(
            RestaurantId restaurantId,
            UserId userId,
            RestaurantMemberStatus status
    );


    boolean existsByRestaurantIdAndUserIdAndRoleAndStatus(
            RestaurantId restaurantId,
            UserId userId,
            RestaurantMemberRole role,
            RestaurantMemberStatus status
    );

}