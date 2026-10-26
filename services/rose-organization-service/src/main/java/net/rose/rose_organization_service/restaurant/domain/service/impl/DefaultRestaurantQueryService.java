package net.rose.rose_organization_service.restaurant.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.core.exception.NotFoundException;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantDetails;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantStatus;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantQueryService;
import net.rose.rose_organization_service.restaurant.persistence.entity.RestaurantEntity;
import net.rose.rose_organization_service.restaurant.persistence.repository.api.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultRestaurantQueryService implements RestaurantQueryService {

    private final RestaurantRepository repository;


    @Override
    @Transactional(readOnly = true)
    public RestaurantDetails getById(RestaurantId restaurantId) {
        return repository.findById(restaurantId.getId())
                .filter(entity -> !entity.isArchived())
                .map(this::toDetails)
                .orElseThrow(() -> NotFoundException.of("Restaurant", restaurantId));
    }


    @Override
    @Transactional(readOnly = true)
    public List<RestaurantDetails> getByIds(Collection<RestaurantId> restaurantIds) {
        List<UUID> ids = restaurantIds.stream()
                .map(RestaurantId::getId)
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        return repository.findAllByIdInAndStatus(ids, RestaurantStatus.ACTIVE)
                .stream()
                .map(this::toDetails)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsActive(RestaurantId restaurantId) {
        return repository.findById(restaurantId.getId())
                .filter(entity -> !entity.isArchived())
                .isPresent();
    }


    private RestaurantDetails toDetails(RestaurantEntity entity) {
        return new RestaurantDetails(
                RestaurantId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getTaxId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}