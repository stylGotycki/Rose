package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.menu.domain.command.MenuItemCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuItemUpdateCommand;
import net.rose.rose_menu_service.menu.domain.model.MenuCategoryStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuItemStatus;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuItemCommandService;
import net.rose.rose_menu_service.menu.integration.publisher.MenuItemChangedPublisher;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuItemEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuCategoryRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuItemRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultMenuItemCommandService
        implements MenuItemCommandService {

    private final MenuItemRepository itemRepository;
    private final MenuCategoryRepository categoryRepository;
    private final MenuRepository menuRepository;
    private final RestaurantAccessGuard restaurantAccessGuard;
    private final MenuItemChangedPublisher itemChangedPublisher;

    @Override
    @Transactional
    public MenuItemEntity create(MenuItemCreateCommand command) {
        MenuCategoryEntity category =
                findCategory(command.categoryId().getId());

        MenuEntity menu = findMenu(category.getMenuId().getId());

        requireMenuManagement(menu);
        validatePrice(command.price());

        MenuItemEntity item = MenuItemEntity.builder()
                .categoryId(command.categoryId())
                .name(command.name())
                .description(command.description())
                .price(command.price())
                .currency(command.currency())
                .available(command.available())
                .status(MenuItemStatus.ACTIVE)
                .build();

        MenuItemEntity savedItem =
                itemRepository.save(item);

        itemChangedPublisher.publish(
                savedItem,
                category,
                menu
        );

        return savedItem;
    }

    @Override
    @Transactional
    public MenuItemEntity update(
            UUID itemId,
            MenuItemUpdateCommand command
    ) {
        MenuItemEntity item = findItem(itemId);
        MenuCategoryEntity category =
                findCategory(item.getCategoryId().getId());
        MenuEntity menu = findMenu(category.getMenuId().getId());

        requireMenuManagement(menu);
        validatePrice(command.price());

        item.setName(command.name());
        item.setDescription(command.description());
        item.setPrice(command.price());
        item.setCurrency(command.currency());
        item.setAvailable(command.available());

        MenuItemEntity savedItem =
                itemRepository.save(item);

        itemChangedPublisher.publish(
                savedItem,
                category,
                menu
        );

        return savedItem;
    }

    @Override
    @Transactional
    public void archive(UUID itemId) {
        MenuItemEntity item = findItem(itemId);
        MenuCategoryEntity category =
                findCategory(item.getCategoryId().getId());
        MenuEntity menu = findMenu(category.getMenuId().getId());

        requireMenuManagement(menu);

        item.archive();

        MenuItemEntity savedItem =
                itemRepository.save(item);

        itemChangedPublisher.publish(
                savedItem,
                category,
                menu
        );
    }

    private MenuItemEntity findItem(UUID itemId) {
        return itemRepository.findByIdAndStatus(
                        itemId,
                        MenuItemStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new NotFoundException("Menu item not found")
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

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException(
                    "Menu item price cannot be negative"
            );
        }
    }
}