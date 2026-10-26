package net.rose.rose_access_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_access_service.access.domain.model.RestaurantMemberStatus;
import net.rose.rose_access_service.access.domain.service.api.RestaurantAccessQueryService;
import net.rose.rose_access_service.access.persistence.entity.RestaurantMemberEntity;
import net.rose.rose_access_service.access.persistence.repository.api.RestaurantMemberRepository;
import net.rose.rose_access_service.core.config.security.CurrentUserProvider;
import net.rose.rose_access_service.core.exception.ForbiddenException;
import net.rose.rose_access_service.core.exception.NotFoundException;
import net.rose.rose_access_service.core.persistence.id.RestaurantId;
import net.rose.rose_access_service.core.persistence.id.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultRestaurantAccessQueryService implements RestaurantAccessQueryService {

    private final RestaurantMemberRepository repository;


    private final CurrentUserProvider currentUserProvider;


    @Override
    @Transactional(readOnly = true)
    public List<RestaurantMemberEntity> findActiveByRestaurantId(RestaurantId restaurantId) {
        currentUserProvider.getCurrentUser();

        return repository.findAllByRestaurantIdAndStatusOrderByCreatedAtAsc(
                restaurantId,
                RestaurantMemberStatus.ACTIVE
        );
    }


    @Override
    @Transactional(readOnly = true)
    public RestaurantMemberEntity findCurrentActiveMember(RestaurantId restaurantId) {
        UserId currentUserId = currentUserProvider.getCurrentUserId();

        return repository.findByRestaurantIdAndUserIdAndStatus(
                        restaurantId,
                        currentUserId,
                        RestaurantMemberStatus.ACTIVE
                )
                .orElseThrow(() -> new ForbiddenException("User has no active access to this restaurant.")
                );
    }


    @Override
    @Transactional(readOnly = true)
    public RestaurantMemberEntity findActiveById(UUID memberId) {
        currentUserProvider.getCurrentUser();

        return repository.findByIdAndStatus(memberId, RestaurantMemberStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Restaurant member not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<RestaurantMemberRole> findActiveRole(RestaurantId restaurantId, UserId userId) {
        return repository.findByRestaurantIdAndUserIdAndStatus(
                        restaurantId,
                        userId,
                        RestaurantMemberStatus.ACTIVE
                )
                .map(RestaurantMemberEntity::getRole);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveAccess(RestaurantId restaurantId, UserId userId) {
        return repository.existsByRestaurantIdAndUserIdAndStatus(
                restaurantId,
                userId,
                RestaurantMemberStatus.ACTIVE
        );
    }


    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveRole(
            RestaurantId restaurantId,
            UserId userId,
            RestaurantMemberRole role
    ) {
        return repository.existsByRestaurantIdAndUserIdAndRoleAndStatus(
                restaurantId,
                userId,
                role,
                RestaurantMemberStatus.ACTIVE
        );
    }
}