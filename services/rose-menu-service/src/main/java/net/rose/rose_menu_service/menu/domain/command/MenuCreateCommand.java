package net.rose.rose_menu_service.menu.domain.command;

import net.rose.rose_menu_service.core.persistence.id.RestaurantId;

public record MenuCreateCommand(
        RestaurantId restaurantId,
        String name,
        String description
) {
}