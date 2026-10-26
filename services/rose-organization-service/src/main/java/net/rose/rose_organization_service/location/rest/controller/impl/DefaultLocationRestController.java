package net.rose.rose_organization_service.location.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_organization_service.location.domain.model.LocationDetails;
import net.rose.rose_organization_service.location.domain.service.api.LocationCommandService;
import net.rose.rose_organization_service.location.domain.service.api.LocationQueryService;
import net.rose.rose_organization_service.location.rest.controller.api.LocationRestController;
import net.rose.rose_organization_service.location.rest.dto.LocationCreateDto;
import net.rose.rose_organization_service.location.rest.dto.LocationReadDto;
import net.rose.rose_organization_service.location.rest.dto.LocationUpdateDto;
import net.rose.rose_organization_service.location.rest.mapper.LocationRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultLocationRestController
        implements LocationRestController {

    private final LocationCommandService commandService;
    private final LocationQueryService queryService;
    private final LocationRestMapper mapper;

    @Override
    public ResponseEntity<Void> createLocation(
            UUID restaurantId,
            LocationCreateDto dto
    ) {
        LocationId locationId = commandService.create(
                mapper.toCommand(
                        dto,
                        RestaurantId.of(restaurantId)
                )
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/locations/"
                                        + locationId.getId()
                        )
                )
                .build();
    }

    @Override
    public ResponseEntity<CollectionRestDto<LocationReadDto>>
    getLocationsForRestaurant(
            UUID restaurantId
    ) {
        List<LocationReadDto> locations =
                queryService.getAllForRestaurant(
                                RestaurantId.of(restaurantId)
                        )
                        .stream()
                        .map(mapper::toDto)
                        .toList();

        return ResponseEntity.ok(
                new CollectionRestDto<>(locations)
        );
    }

    @Override
    public ResponseEntity<LocationReadDto> getLocation(
            UUID locationId
    ) {
        LocationDetails details =
                queryService.getById(
                        LocationId.of(locationId)
                );

        return ResponseEntity.ok(
                mapper.toDto(details)
        );
    }

    @Override
    public ResponseEntity<Void> updateLocation(
            UUID locationId,
            LocationUpdateDto dto
    ) {
        commandService.update(
                mapper.toCommand(
                        dto,
                        LocationId.of(locationId)
                )
        );

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> archiveLocation(
            UUID locationId
    ) {
        commandService.archive(
                LocationId.of(locationId)
        );

        return ResponseEntity.noContent().build();
    }
}