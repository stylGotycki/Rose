package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuItemQueryService;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuCategoryRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuItemRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultMenuItemQueryService
        implements MenuItemQueryService {

    private final MenuItemRepository itemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemEntity> findActiveByCategoryId(
            MenuCategoryId categoryId
    ) {
        MenuCategoryEntity category =
                findCategory(categoryId.getId());

        MenuEntity menu = findMenu(category.getMenuId().getId());

        restaurantAccessGuard.requireActiveAccess(menu.getRestaurantId());

        return itemRepository
                .findAllByCategoryIdAndStatusOrderByNameAsc(
                        categoryId,
                        MenuItemStatus.ACTIVE
                );
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemEntity findActiveById(UUID itemId) {
        MenuItemEntity item = itemRepository.findByIdAndStatus(
                        itemId,
                        MenuItemStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new NotFoundException("Menu item not found")
                );

        MenuCategoryEntity category =
                findCategory(item.getCategoryId().getId());

        MenuEntity menu = findMenu(category.getMenuId().getId());

        restaurantAccessGuard.requireActiveAccess(menu.getRestaurantId());

        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemEntity findAvailableByRestaurantIdAndId(
            RestaurantId restaurantId,
            UUID itemId
    ) {
        return itemRepository.findAvailableByRestaurantIdAndId(
                        restaurantId.getId(),
                        itemId
                )
                .orElseThrow(() -> new NotFoundException(
                        "Available menu item not found"
                ));
    }


    private MenuCategoryEntity findCategory(UUID categoryId) {
        return categoryRepository.findByIdAndStatus(
                        categoryId,
                        MenuCategoryStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new NotFoundException("Menu category not found")
                );
    }

    private MenuEntity findMenu(UUID menuId) {
        return menuRepository.findByIdAndStatusNot(
                        menuId,
                        MenuStatus.ARCHIVED
                )
                .orElseThrow(() -> new NotFoundException("Menu not found"));
    }
}