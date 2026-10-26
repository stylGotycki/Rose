package net.rose.rose_menu_service.menu.persistence.repository.api;

import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuCategoryRepository extends JpaRepository<MenuCategoryEntity, UUID> {

    List<MenuCategoryEntity> findAllByMenuIdAndStatusOrderByDisplayOrderAsc(
            MenuId menuId,
            MenuCategoryStatus status
    );


    Optional<MenuCategoryEntity> findByIdAndStatus(
            UUID id,
            MenuCategoryStatus status
    );

    List<MenuCategoryEntity> findAllByMenuId(
            MenuId menuId
    );

}