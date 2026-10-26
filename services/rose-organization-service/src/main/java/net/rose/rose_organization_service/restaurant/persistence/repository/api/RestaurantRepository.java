package net.rose.rose_organization_service.restaurant.persistence.repository.api;

import net.rose.rose_organization_service.restaurant.domain.model.RestaurantStatus;
import net.rose.rose_organization_service.restaurant.persistence.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, UUID> {

    List<RestaurantEntity> findAllByIdInAndStatus(
            Collection<UUID> ids,
            RestaurantStatus status
    );
}