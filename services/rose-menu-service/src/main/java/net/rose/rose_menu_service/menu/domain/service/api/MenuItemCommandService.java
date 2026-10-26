package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.menu.domain.command.MenuItemCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuItemUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;

import java.util.UUID;

public interface MenuItemCommandService {

    MenuItemEntity create(MenuItemCreateCommand command);


    MenuItemEntity update(UUID itemId, MenuItemUpdateCommand command);


    void archive(UUID itemId);

}