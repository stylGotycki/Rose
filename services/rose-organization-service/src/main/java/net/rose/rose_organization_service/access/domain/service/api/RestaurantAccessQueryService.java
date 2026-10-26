package net.rose.rose_organization_service.access.domain.service.api;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.core.persistence.id.UserId;

import java.util.List;

public interface RestaurantAccessQueryService {

    List<RestaurantId> getRestaurantIdsForUser(UserId userId);
}