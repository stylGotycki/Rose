package net.rose.rose_order_service.menu.persistence.repository.api;

import net.rose.rose_order_service.menu.persistence.entity.MenuItemProjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuItemProjectionRepository
        extends JpaRepository<MenuItemProjectionEntity, UUID> {

    Optional<MenuItemProjectionEntity>
    findByMenuItemIdAndRestaurantId(
            UUID menuItemId,
            UUID restaurantId
    );
}