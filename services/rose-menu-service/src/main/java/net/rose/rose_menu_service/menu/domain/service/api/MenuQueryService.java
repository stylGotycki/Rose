package net.rose.rose_menu_service.menu.domain.service.api;

import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;

import java.util.List;
import java.util.UUID;

public interface MenuQueryService {

    List<MenuEntity> findAllByRestaurantId(RestaurantId restaurantId);


    MenuEntity findVisibleById(UUID menuId);

}