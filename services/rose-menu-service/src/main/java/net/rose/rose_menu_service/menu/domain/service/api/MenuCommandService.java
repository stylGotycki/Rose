package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.menu.domain.command.MenuCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;

import java.util.UUID;

public interface MenuCommandService {

    MenuEntity create(MenuCreateCommand command);


    MenuEntity update(UUID menuId, MenuUpdateCommand command);


    void archive(UUID menuId);


    MenuEntity activate(UUID menuId);

}