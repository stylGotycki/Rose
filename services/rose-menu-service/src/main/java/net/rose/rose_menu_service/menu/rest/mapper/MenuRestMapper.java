package net.rose.rose_menu_service.menu.rest.mapper;

import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.domain.command.MenuCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.rest.dto.MenuCreateDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuReadDto;
import net.rose.rose_menu_service.menu.rest.dto.MenuUpdateDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MenuRestMapper {

    public MenuCreateCommand toCommand(UUID restaurantId, MenuCreateDto dto) {
        return new MenuCreateCommand(
                RestaurantId.of(restaurantId),
                dto.name(),
                dto.description()
        );
    }


    public MenuUpdateCommand toCommand(MenuUpdateDto dto) {
        return new MenuUpdateCommand(
                dto.name(),
                dto.description()
        );
    }


    public MenuReadDto toDto(MenuEntity entity) {
        return new MenuReadDto(
                entity.getId(),
                entity.getRestaurantId().getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}