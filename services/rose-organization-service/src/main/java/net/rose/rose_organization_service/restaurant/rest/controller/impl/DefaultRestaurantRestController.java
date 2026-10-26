package net.rose.rose_organization_service.restaurant.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_organization_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_organization_service.access.domain.service.api.RestaurantAccessQueryService;
import net.rose.rose_organization_service.core.config.security.CurrentUser;
import net.rose.rose_organization_service.core.config.security.CurrentUserProvider;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantDetails;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantCommandService;
import net.rose.rose_organization_service.restaurant.domain.service.api.RestaurantQueryService;
import net.rose.rose_organization_service.restaurant.rest.controller.api.RestaurantRestController;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantCreateDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantReadDto;
import net.rose.rose_organization_service.restaurant.rest.dto.RestaurantUpdateDto;
import net.rose.rose_organization_service.restaurant.rest.mapper.RestaurantRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultRestaurantRestController
        implements RestaurantRestController {

    private final RestaurantCommandService commandService;
    private final RestaurantQueryService queryService;
    private final RestaurantRestMapper mapper;
    private final CurrentUserProvider currentUserProvider;
    private final RestaurantAccessQueryService accessQueryService;
    private final RestaurantAccessGuard accessGuard;

    @Override
    public ResponseEntity<Void> createRestaurant(
            RestaurantCreateDto dto
    ) {
        CurrentUser currentUser =
                currentUserProvider.getCurrentUser();

        RestaurantId restaurantId = commandService.create(
                mapper.toCommand(
                        dto,
                        currentUser.userId()
                )
        );

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/restaurants/"
                                        + restaurantId.getId()
                        )
                )
                .build();
    }

    @Override
    public ResponseEntity<CollectionRestDto<RestaurantReadDto>>
    getRestaurants() {
        CurrentUser currentUser =
                currentUserProvider.getCurrentUser();

        List<RestaurantId> restaurantIds =
                accessQueryService.getRestaurantIdsForUser(
                        currentUser.userId()
                );

        List<RestaurantReadDto> restaurants =
                queryService.getByIds(restaurantIds)
                        .stream()
                        .map(mapper::toDto)
                        .toList();

        return ResponseEntity.ok(
                new CollectionRestDto<>(restaurants)
        );
    }

    @Override
    public ResponseEntity<RestaurantReadDto> getRestaurant(
            UUID restaurantId
    ) {
        RestaurantId id = RestaurantId.of(restaurantId);

        accessGuard.requireActiveAccess(id);

        RestaurantDetails details =
                queryService.getById(id);

        return ResponseEntity.ok(
                mapper.toDto(details)
        );
    }

    @Override
    public ResponseEntity<Void> updateRestaurant(
            UUID restaurantId,
            RestaurantUpdateDto dto
    ) {
        RestaurantId id = RestaurantId.of(restaurantId);

        accessGuard.requireAnyRole(
                id,
                RestaurantMemberRole.OWNER
        );

        commandService.update(
                id,
                mapper.toCommand(dto)
        );

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> archiveRestaurant(
            UUID restaurantId
    ) {
        RestaurantId id = RestaurantId.of(restaurantId);

        accessGuard.requireAnyRole(
                id,
                RestaurantMemberRole.OWNER
        );

        commandService.archive(id);

        return ResponseEntity.noContent().build();
    }
}