package net.rose.rose_organization_service.access.persistence.repository.api;

import net.rose.rose_organization_service.access.persistence.entity.RestaurantAccessProjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantAccessProjectionRepository
        extends JpaRepository<RestaurantAccessProjectionEntity, UUID> {

    Optional<RestaurantAccessProjectionEntity>
    findByRestaurantIdAndUserId(
            UUID restaurantId,
            UUID userId
    );

    List<RestaurantAccessProjectionEntity>
    findAllByUserIdAndStatus(
            UUID userId,
            String status
    );
}