package net.rose.rose_menu_service.menu.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCommandService;
import net.rose.rose_menu_service.menu.domain.service.api.MenuQueryService;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.rest.controller.api.MenuRestController;
import net.rose.rose_menu_service.menu.rest.dto.MenuCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuUpdateDto;
import net.rose.rose_menu_service.menu.rest.mapper.MenuRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultMenuRestController implements MenuRestController {

    private final MenuCommandService commandService;


    private final MenuQueryService queryService;


    private final MenuRestMapper mapper;


    @Override
    public MenuReadDto create(UUID restaurantId, MenuCreateDto dto) {
        MenuEntity menu = commandService.create(
                mapper.toCommand(restaurantId, dto)
        );

        return mapper.toDto(menu);
    }


    @Override
    public CollectionRestDto<MenuReadDto> getAllByRestaurant(UUID restaurantId) {
        return new CollectionRestDto<>(
                queryService.findAllByRestaurantId(RestaurantId.of(restaurantId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public MenuReadDto getById(UUID menuId) {
        return mapper.toDto(
                queryService.findVisibleById(menuId)
        );
    }


    @Override
    public MenuReadDto update(UUID menuId, MenuUpdateDto dto) {
        return mapper.toDto(
                commandService.update(menuId, mapper.toCommand(dto))
        );
    }


    @Override
    public MenuReadDto activate(UUID menuId) {
        return mapper.toDto(
                commandService.activate(menuId)
        );
    }


    @Override
    public void archive(UUID menuId) {
        commandService.archive(menuId);
    }
}