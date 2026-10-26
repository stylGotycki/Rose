package net.rose.rose_menu_service.access.domain.service.api;

import net.rose.rose_menu_service.access.integration.event.RestaurantMemberChangedEvent;

public interface RestaurantAccessProjectionService {

    void apply(RestaurantMemberChangedEvent event);
}