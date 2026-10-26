package net.rose.rose_menu_service.menu.rest.mapper;

import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.menu.domain.command.MenuCategoryCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuCategoryUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuCategoryUpdateDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuCategoryRestMapper {

    public MenuCategoryCreateCommand toCommand(UUID menuId, MenuCategoryCreateDto dto) {
        return new MenuCategoryCreateCommand(
                MenuId.of(menuId),
                dto.name(),
                dto.displayOrder()
        );
    }


    public MenuCategoryUpdateCommand toCommand(MenuCategoryUpdateDto dto) {
        return new MenuCategoryUpdateCommand(
                dto.name(),
                dto.displayOrder()
        );
    }


    public MenuCategoryReadDto toDto(MenuCategoryEntity entity) {
        return new MenuCategoryReadDto(
                entity.getId(),
                entity.getMenuId().getId(),
                entity.getName(),
                entity.getDisplayOrder(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}