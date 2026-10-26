package net.rose.rose_menu_service.menu.rest.mapper;

import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.menu.domain.command.MenuItemCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuItemUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuItemUpdateDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuItemRestMapper {

    public MenuItemCreateCommand toCommand(UUID categoryId, MenuItemCreateDto dto) {
        return new MenuItemCreateCommand(
                MenuCategoryId.of(categoryId),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.available()
        );
    }


    public MenuItemUpdateCommand toCommand(MenuItemUpdateDto dto) {
        return new MenuItemUpdateCommand(
                dto.name(),
                dto.description(),
                dto.price(),
                dto.currency(),
                dto.available()
        );
    }


    public MenuItemReadDto toDto(MenuItemEntity entity) {
        return new MenuItemReadDto(
                entity.getId(),
                entity.getCategoryId().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCurrency(),
                entity.getAvailable(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}