package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;

import java.util.List;
import java.util.UUID;

public interface MenuCategoryQueryService {

    List<MenuCategoryEntity> findActiveByMenuId(MenuId menuId);


    MenuCategoryEntity findActiveById(UUID categoryId);

}