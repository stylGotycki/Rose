package net.rose.rose_menu_service.menu.persistence.repository.api;

import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuEntity, UUID> {

    List<MenuEntity> findAllByRestaurantIdAndStatusNotOrderByCreatedAtDesc(
            RestaurantId restaurantId,
            MenuStatus status
    );


    Optional<MenuEntity> findByIdAndStatusNot(
            UUID id,
            MenuStatus status
    );


    boolean existsByRestaurantIdAndNameAndStatusNot(
            RestaurantId restaurantId,
            String name,
            MenuStatus status
    );
}