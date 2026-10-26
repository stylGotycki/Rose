package net.rose.rose_organization_service.location.persistence.repository.api;

import net.rose.rose_organization_service.core.persistence.id.RestaurantId;
import net.rose.rose_organization_service.location.domain.model.LocationStatus;
import net.rose.rose_organization_service.location.persistence.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {

    List<LocationEntity> findAllByRestaurantIdAndStatus(
            RestaurantId restaurantId,
            LocationStatus status
    );


    Optional<LocationEntity> findByIdAndRestaurantId(
            UUID id,
            RestaurantId restaurantId
    );

}