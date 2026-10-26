package net.rose.rose_menu_service.menu.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.domain.service.api.MenuItemCommandService;
import net.rose.rose_menu_service.menu.domain.service.api.MenuItemQueryService;
import net.rose.rose_menu_service.menu.rest.controller.api.MenuItemRestController;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemUpdateDto;
import net.rose.rose_menu_service.menu.rest.mapper.MenuItemRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultMenuItemRestController implements MenuItemRestController {

    private final MenuItemCommandService commandService;


    private final MenuItemQueryService queryService;


    private final MenuItemRestMapper mapper;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    public MenuItemReadDto create(UUID categoryId, MenuItemCreateDto dto) {
        return mapper.toDto(
                commandService.create(mapper.toCommand(categoryId, dto))
        );
    }


    @Override
    public CollectionRestDto<MenuItemReadDto> getAllByCategory(UUID categoryId) {
        return new CollectionRestDto<>(
                queryService.findActiveByCategoryId(MenuCategoryId.of(categoryId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public MenuItemReadDto getById(UUID itemId) {
        return mapper.toDto(
                queryService.findActiveById(itemId)
        );
    }


    @Override
    public MenuItemReadDto update(UUID itemId, MenuItemUpdateDto dto) {
        return mapper.toDto(
                commandService.update(itemId, mapper.toCommand(dto))
        );
    }


    @Override
    public MenuItemReadDto getAvailableByRestaurant(
            UUID restaurantId,
            UUID itemId
    ) {
        RestaurantId wrappedRestaurantId = RestaurantId.of(restaurantId);

        restaurantAccessGuard.requireActiveAccess(wrappedRestaurantId);

        return mapper.toDto(
                queryService.findAvailableByRestaurantIdAndId(
                        wrappedRestaurantId,
                        itemId
                )
        );
    }


    @Override
    public void archive(UUID itemId) {
        commandService.archive(itemId);
    }
}