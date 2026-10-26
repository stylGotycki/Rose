package net.rose.rose_organization_service.restaurant.domain.service.api;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.restaurant.domain.model.RestaurantDetails;

import java.util.Collection;
import java.util.List;

public interface RestaurantQueryService {

    RestaurantDetails getById(RestaurantId restaurantId);


    List<RestaurantDetails> getByIds(Collection<RestaurantId> restaurantIds);


    boolean existsActive(RestaurantId restaurantId);

}