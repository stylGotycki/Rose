package net.rose.rose_menu_service.menu.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCategoryCommandService;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCategoryQueryService;
import net.rose.rose_menu_service.menu.rest.controller.api.MenuCategoryRestController;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryUpdateDto;
import net.rose.rose_menu_service.menu.rest.mapper.MenuCategoryRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultMenuCategoryRestController implements MenuCategoryRestController {

    private final MenuCategoryCommandService commandService;


    private final MenuCategoryQueryService queryService;


    private final MenuCategoryRestMapper mapper;


    @Override
    public MenuCategoryReadDto create(UUID menuId, MenuCategoryCreateDto dto) {
        return mapper.toDto(
                commandService.create(mapper.toCommand(menuId, dto))
        );
    }


    @Override
    public CollectionRestDto<MenuCategoryReadDto> getAllByMenu(UUID menuId) {
        return new CollectionRestDto<>(
                queryService.findActiveByMenuId(MenuId.of(menuId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public MenuCategoryReadDto getById(UUID categoryId) {
        return mapper.toDto(
                queryService.findActiveById(categoryId)
        );
    }


    @Override
    public MenuCategoryReadDto update(UUID categoryId, MenuCategoryUpdateDto dto) {
        return mapper.toDto(
                commandService.update(categoryId, mapper.toCommand(dto))
        );
    }


    @Override
    public void archive(UUID categoryId) {
        commandService.archive(categoryId);
    }
}