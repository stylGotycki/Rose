package net.rose.rose_organization_service.location.domain.event;

import net.rose.rose_organization_service.core.persistence.id.LocationId;
import net.rose.rose_organization_service.core.persistence.id.RestaurantId;

public record LocationUpdatedEvent(
        LocationId locationId,
        RestaurantId restaurantId
) {
}