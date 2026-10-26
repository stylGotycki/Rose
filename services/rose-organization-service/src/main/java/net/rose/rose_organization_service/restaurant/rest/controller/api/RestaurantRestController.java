package net.rose.rose_organization_service.restaurant.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_organization_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantCreateDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantReadDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantUpdateDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface RestaurantRestController {

    @PostMapping(
            path = "/api/restaurants",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> createRestaurant(
            @RequestBody @Valid RestaurantCreateDto dto
    );


    @GetMapping(
            path = "/api/restaurants",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CollectionRestDto<RestaurantReadDto>> getRestaurants();


    @GetMapping(
            path = "/api/restaurants/{restaurantId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<RestaurantReadDto> getRestaurant(
            @PathVariable UUID restaurantId
    );


    @PutMapping(
            path = "/api/restaurants/{restaurantId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> updateRestaurant(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid RestaurantUpdateDto dto
    );


    @DeleteMapping(
            path = "/api/restaurants/{restaurantId}"
    )
    ResponseEntity<Void> archiveRestaurant(
            @PathVariable UUID restaurantId
    );

}