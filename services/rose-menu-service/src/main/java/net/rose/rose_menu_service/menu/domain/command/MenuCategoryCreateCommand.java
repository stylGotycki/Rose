package net.rose.rose_menu_service.menu.domain.command;

import net.rose.rose_menu_service.core.persistence.id.MenuId;

public record MenuCategoryCreateCommand(
        MenuId menuId,
        String name,
        Integer displayOrder
) {
}