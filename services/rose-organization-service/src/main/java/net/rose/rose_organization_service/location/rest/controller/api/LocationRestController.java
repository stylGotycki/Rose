package net.rose.rose_organization_service.location.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_organization_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_organization_service.location.rest.dto.LocationCreateDto;
import net.rose.rose_organization_service.location.rest.dto.LocationReadDto;
import net.rose.rose_organization_service.location.rest.dto.LocationUpdateDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface LocationRestController {

    @PostMapping(
            path = "/api/restaurants/{restaurantId}/locations",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> createLocation(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid LocationCreateDto dto
    );


    @GetMapping(
            path = "/api/restaurants/{restaurantId}/locations",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CollectionRestDto<LocationReadDto>> getLocationsForRestaurant(
            @PathVariable UUID restaurantId
    );


    @GetMapping(
            path = "/api/locations/{locationId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<LocationReadDto> getLocation(
            @PathVariable UUID locationId
    );


    @PutMapping(
            path = "/api/locations/{locationId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> updateLocation(
            @PathVariable UUID locationId,
            @RequestBody @Valid LocationUpdateDto dto
    );


    @DeleteMapping(
            path = "/api/locations/{locationId}"
    )
    ResponseEntity<Void> archiveLocation(
            @PathVariable UUID locationId
    );

}