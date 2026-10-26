package net.rose.rose_organization_service.access.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessQueryService;
import net.rose.rose_organization_service.access.persistence.entity.RestaurantAccessProjectionEntity;
import net.rose.rose_organization_service.access.persistence.repository.api.RestaurantAccessProjectionRepository;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.core.persistence.id.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class DefaultRestaurantAccessQueryService
        implements RestaurantAccessQueryService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final RestaurantAccessProjectionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<RestaurantId> getRestaurantIdsForUser(UserId userId) {
        return repository
                .findAllByUserIdAndStatus(
                        userId.getId(),
                        ACTIVE_STATUS
                )
                .stream()
                .map(RestaurantAccessProjectionEntity::getRestaurantId)
                .map(RestaurantId::of)
                .toList();
    }
}