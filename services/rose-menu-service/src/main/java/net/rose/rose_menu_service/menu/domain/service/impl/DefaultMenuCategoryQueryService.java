package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.core.persistence.id.MenuId;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCategoryQueryService;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuCategoryRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultMenuCategoryQueryService
        implements MenuCategoryQueryService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;

    @Override
    @Transactional(readOnly = true)
    public List<MenuCategoryEntity> findActiveByMenuId(MenuId menuId) {
        MenuEntity menu = findMenu(menuId.getId());

        restaurantAccessGuard.requireActiveAccess(menu.getRestaurantId());

        return categoryRepository
                .findAllByMenuIdAndStatusOrderByDisplayOrderAsc(
                        menuId,
                        MenuCategoryStatus.ACTIVE
                );
    }

    @Override
    @Transactional(readOnly = true)
    public MenuCategoryEntity findActiveById(UUID categoryId) {
        MenuCategoryEntity category = categoryRepository.findByIdAndStatus(
                        categoryId,
                        MenuCategoryStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new NotFoundException("Menu category not found")
                );

        MenuEntity menu = findMenu(category.getMenuId().getId());

        restaurantAccessGuard.requireActiveAccess(menu.getRestaurantId());

        return category;
    }

    private MenuEntity findMenu(UUID menuId) {
        return menuRepository.findByIdAndStatusNot(
                        menuId,
                        MenuStatus.ARCHIVED
                )
                .orElseThrow(() -> new NotFoundException("Menu not found"));
    }
}