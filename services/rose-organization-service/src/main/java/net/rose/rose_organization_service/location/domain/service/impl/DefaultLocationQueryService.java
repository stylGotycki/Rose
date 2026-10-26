package net.rose.rose_organization_service.location.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_organization_service.core.exception.NotFoundException;
import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.model.LocationDetails;
import net.rose.rose_organization_service.location.domain.model.LocationStatus;
import net.rose.rose_organization_service.location.domain.service.api.LocationQueryService;
import net.rose.rose_organization_service.location.persistence.entity.LocationEntity;
import net.rose.rose_organization_service.location.persistence.repository.api.LocationRepository;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class DefaultLocationQueryService
        implements LocationQueryService {

    private final LocationRepository repository;
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    @Transactional(readOnly = true)
    public LocationDetails getById(
            LocationId locationId
    ) {
        LocationEntity entity = repository
                .findById(locationId.getId())
                .filter(location ->
                        !location.isArchived()
                )
                .orElseThrow(() ->
                        NotFoundException.of(
                                "Location",
                                locationId
                        )
                );

        ensureRestaurantExists(
                entity.getRestaurantId()
        );

        restaurantAccessGuard.requireActiveAccess(
                entity.getRestaurantId()
        );

        return toDetails(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationDetails> getAllForRestaurant(
            RestaurantId restaurantId
    ) {
        ensureRestaurantExists(restaurantId);

        restaurantAccessGuard.requireActiveAccess(
                restaurantId
        );

        return repository
                .findAllByRestaurantIdAndStatus(
                        restaurantId,
                        LocationStatus.ACTIVE
                )
                .stream()
                .map(this::toDetails)
                .toList();
    }

    private LocationDetails toDetails(
            LocationEntity entity
    ) {
        return new LocationDetails(
                LocationId.of(entity.getId()),
                entity.getRestaurantId(),
                entity.getName(),
                entity.getCity(),
                entity.getStreet(),
                entity.getBuildingNumber(),
                entity.getPostalCode(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void ensureRestaurantExists(
            RestaurantId restaurantId
    ) {
        if (!restaurantQueryService
                .existsActive(restaurantId)) {
            throw NotFoundException.of(
                    "Restaurant",
                    restaurantId
            );
        }
    }
}