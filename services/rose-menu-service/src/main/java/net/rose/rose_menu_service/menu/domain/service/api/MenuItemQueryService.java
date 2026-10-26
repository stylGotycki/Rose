package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;

import java.util.List;
import java.util.UUID;

public interface MenuItemQueryService {

    List<MenuItemEntity> findActiveByCategoryId(MenuCategoryId categoryId);


    MenuItemEntity findActiveById(UUID itemId);


    MenuItemEntity findAvailableByRestaurantIdAndId(
            RestaurantId restaurantId,
            UUID itemId
    );

}