package net.rose.rose_organization_service.restaurant.domain.service.api;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.restaurant.domain.command.CreateRestaurantCommand;
import net.rose.rose_organization_service.restaurant.domain.command.UpdateRestaurantCommand;

public interface RestaurantCommandService {

    RestaurantId create(CreateRestaurantCommand command);


    void update(RestaurantId restaurantId, UpdateRestaurantCommand command);


    void archive(RestaurantId restaurantId);

}