package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.core.persistence.id.MenuCategoryId;
import net.rose.rose_menu_service.menu.domain.command.MenuCategoryCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuCategoryUpdateCommand;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCategoryCommandService;
import net.rose.rose_menu_service.menu.integration.publisher.MenuItemChangedPublisher;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuCategoryRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuItemRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultMenuCategoryCommandService
        implements MenuCategoryCommandService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;
    private final MenuItemRepository itemRepository;
    private final MenuItemChangedPublisher itemChangedPublisher;


    @Override
    @Transactional
    public MenuCategoryEntity create(
            MenuCategoryCreateCommand command
    ) {
        MenuEntity menu = findMenu(command.menuId().getId());

        requireMenuManagement(menu);

        MenuCategoryEntity category = MenuCategoryEntity.builder()
                .menuId(command.menuId())
                .name(command.name())
                .displayOrder(command.displayOrder())
                .status(MenuCategoryStatus.ACTIVE)
                .build();

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public MenuCategoryEntity update(
            UUID categoryId,
            MenuCategoryUpdateCommand command
    ) {
        MenuCategoryEntity category = findCategory(categoryId);
        MenuEntity menu = findMenu(category.getMenuId().getId());

        requireMenuManagement(menu);

        category.setName(command.name());
        category.setDisplayOrder(command.displayOrder());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void archive(UUID categoryId) {
        MenuCategoryEntity category =
                findCategory(categoryId);

        MenuEntity menu =
                findMenu(category.getMenuId().getId());

        requireMenuManagement(menu);

        category.archive();

        MenuCategoryEntity savedCategory =
                categoryRepository.save(category);

        itemRepository.findAllByCategoryId(
                        MenuCategoryId.of(categoryId)
                )
                .forEach(item ->
                        itemChangedPublisher.publish(
                                item,
                                savedCategory,
                                menu
                        )
                );
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

    private void requireMenuManagement(MenuEntity menu) {
        restaurantAccessGuard.requireAnyRole(
                menu.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER,
                RestaurantMemberRole.CHEF
        );
    }
}