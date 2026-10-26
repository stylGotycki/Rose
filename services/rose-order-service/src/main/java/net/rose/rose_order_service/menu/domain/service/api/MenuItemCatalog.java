package net.rose.rose_order_service.menu.domain.service.api;

import net.rose.rose_order_service.core.persistence.id.MenuItemId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.menu.domain.model.OrderableMenuItem;

public interface MenuItemCatalog {

    OrderableMenuItem getOrderableItem(
            RestaurantId restaurantId,
            MenuItemId menuItemId
    );
}