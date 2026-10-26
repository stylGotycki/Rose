package net.rose.rose_organization_service.location.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_organization_service.core.exception.NotFoundException;
import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.command.CreateLocationCommand;
import net.rose.rose_organization_service.location.domain.command.UpdateLocationCommand;
import net.rose.rose_organization_service.location.domain.event.LocationArchivedEvent;
import net.rose.rose_organization_service.location.domain.event.LocationCreatedEvent;
import net.rose.rose_organization_service.location.domain.event.LocationUpdatedEvent;
import net.rose.rose_organization_service.location.domain.model.LocationStatus;
import net.rose.rose_organization_service.location.domain.service.api.LocationCommandService;
import net.rose.rose_organization_service.location.persistence.entity.LocationEntity;
import net.rose.rose_organization_service.location.persistence.repository.api.LocationRepository;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultLocationCommandService
        implements LocationCommandService {

    private final LocationRepository repository;
    private final RestaurantQueryService restaurantQueryService;
    private final RestaurantAccessGuard restaurantAccessGuard;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public LocationId create(
            CreateLocationCommand command
    ) {
        ensureRestaurantExists(
                command.restaurantId()
        );

        restaurantAccessGuard.requireAnyRole(
                command.restaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        LocationEntity entity = LocationEntity.builder()
                .restaurantId(command.restaurantId())
                .name(command.name())
                .city(command.city())
                .street(command.street())
                .buildingNumber(command.buildingNumber())
                .postalCode(command.postalCode())
                .status(LocationStatus.ACTIVE)
                .build();

        LocationEntity saved =
                repository.save(entity);

        LocationId locationId =
                LocationId.of(saved.getId());

        eventPublisher.publishEvent(
                new LocationCreatedEvent(
                        locationId,
                        command.restaurantId()
                )
        );

        return locationId;
    }

    @Override
    @Transactional
    public void update(
            UpdateLocationCommand command
    ) {
        LocationEntity entity =
                getActiveEntity(command.locationId());

        ensureRestaurantExists(
                entity.getRestaurantId()
        );

        restaurantAccessGuard.requireAnyRole(
                entity.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        entity.setName(command.name());
        entity.setCity(command.city());
        entity.setStreet(command.street());
        entity.setBuildingNumber(
                command.buildingNumber()
        );
        entity.setPostalCode(
                command.postalCode()
        );

        eventPublisher.publishEvent(
                new LocationUpdatedEvent(
                        command.locationId(),
                        entity.getRestaurantId()
                )
        );
    }

    @Override
    @Transactional
    public void archive(LocationId locationId) {
        LocationEntity entity =
                getActiveEntity(locationId);

        ensureRestaurantExists(
                entity.getRestaurantId()
        );

        restaurantAccessGuard.requireAnyRole(
                entity.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        entity.setStatus(LocationStatus.ARCHIVED);
        entity.setArchivedAt(Instant.now());

        eventPublisher.publishEvent(
                new LocationArchivedEvent(
                        locationId,
                        entity.getRestaurantId()
                )
        );
    }

    private LocationEntity getActiveEntity(
            LocationId locationId
    ) {
        return repository.findById(
                        locationId.getId()
                )
                .filter(entity ->
                        !entity.isArchived()
                )
                .orElseThrow(() ->
                        NotFoundException.of(
                                "Location",
                                locationId
                        )
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