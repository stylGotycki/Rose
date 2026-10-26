package net.rose.rose_organization_service.location.domain.service.api;

import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.model.LocationDetails;

import java.util.List;

public interface LocationQueryService {

    LocationDetails getById(LocationId locationId);

    List<LocationDetails> getAllForRestaurant(
            RestaurantId restaurantId
    );
}