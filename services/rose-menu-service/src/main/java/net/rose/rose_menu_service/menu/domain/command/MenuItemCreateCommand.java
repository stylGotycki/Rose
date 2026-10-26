package net.rose.rose_menu_service.menu.domain.command;

import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;

import java.math.BigDecimal;

public record MenuItemCreateCommand(
        MenuCategoryId categoryId,
        String name,
        String description,
        BigDecimal price,
        String currency,
        Boolean available
) {
}