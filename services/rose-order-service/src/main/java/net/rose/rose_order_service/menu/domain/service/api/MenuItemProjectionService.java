package net.rose.rose_order_service.menu.domain.service.api;

import net.rose.rose_order_service.menu.integration.event.MenuItemChangedEvent;

public interface MenuItemProjectionService {

    void apply(MenuItemChangedEvent event);
}