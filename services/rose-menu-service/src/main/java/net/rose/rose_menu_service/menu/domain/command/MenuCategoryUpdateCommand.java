package net.rose.rose_menu_service.menu.domain.command;

public record MenuCategoryUpdateCommand(
        String name,
        Integer displayOrder
) {
}