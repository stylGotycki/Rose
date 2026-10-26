package net.rose.rose_menu_service.menu.domain.command;

public record MenuUpdateCommand(
        String name,
        String description
) {
}