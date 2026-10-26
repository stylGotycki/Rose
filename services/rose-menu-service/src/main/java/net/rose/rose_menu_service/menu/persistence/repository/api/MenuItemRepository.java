package net.rose.rose_menu_service.menu.persistence.repository.api;

import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, UUID> {

    List<MenuItemEntity> findAllByCategoryIdAndStatusOrderByNameAsc(
            MenuCategoryId categoryId,
            MenuItemStatus status
    );


    Optional<MenuItemEntity> findByIdAndStatus(
            UUID id,
            MenuItemStatus status
    );


    @Query(
            value = """
                    select mi.*
                    from menu_items mi
                    join menu_categories mc
                        on mc.id = mi.category_id
                    join menus m
                        on m.id = mc.menu_id
                    where mi.id = :itemId
                      and m.restaurant_id = :restaurantId
                      and mi.status = 'ACTIVE'
                      and mi.available = true
                    """,
            nativeQuery = true
    )
    Optional<MenuItemEntity> findAvailableByRestaurantIdAndId(
            @Param("restaurantId") UUID restaurantId,
            @Param("itemId") UUID itemId
    );


    List<MenuItemEntity> findAllByCategoryId(
            MenuCategoryId categoryId
    );


    @Query(
            value = """
                select mi.*
                from menu_items mi
                join menu_categories mc
                    on mc.id = mi.category_id
                where mc.menu_id = :menuId
                """,
            nativeQuery = true
    )
    List<MenuItemEntity> findAllByMenuId(
            @Param("menuId") UUID menuId
    );

}