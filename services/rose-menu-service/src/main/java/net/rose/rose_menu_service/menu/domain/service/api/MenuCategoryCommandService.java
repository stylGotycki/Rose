package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.menu.domain.command.MenuCategoryCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuCategoryUpdateCommand;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;

import java.util.UUID;

public interface MenuCategoryCommandService {

    MenuCategoryEntity create(MenuCategoryCreateCommand command);


    MenuCategoryEntity update(UUID categoryId, MenuCategoryUpdateCommand command);


    void archive(UUID categoryId);

}
