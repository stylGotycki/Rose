package net.rose.rose_order_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_order_service.core.exception.NotFoundException;
import net.rose.rose_order_service.core.persistence.id.MenuItemId;
import net.rose.rose_order_service.core.persistence.id.RestaurantId;
import net.rose.rose_order_service.menu.domain.model.OrderableMenuItem;
import net.rose.rose_order_service.menu.domain.service.api.MenuItemCatalog;
import net.rose.rose_order_service.menu.persistence.entity.MenuItemProjectionEntity;
import net.rose.rose_order_service.menu.persistence.repository.api.MenuItemProjectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DefaultMenuItemCatalog implements MenuItemCatalog {

    private final MenuItemProjectionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public OrderableMenuItem getOrderableItem(
            RestaurantId restaurantId,
            MenuItemId menuItemId
    ) {
        MenuItemProjectionEntity item = repository
                .findByMenuItemIdAndRestaurantId(
                        menuItemId.getId(),
                        restaurantId.getId()
                )
                .orElseThrow(() ->
                        new NotFoundException(
                                "Menu item not found"
                        )
                );

        if (!item.isOrderable()) {
            throw new BusinessRuleViolationException(
                    "Menu item is not available for ordering"
            );
        }

        return new OrderableMenuItem(
                item.getMenuItemId(),
                item.getRestaurantId(),
                item.getName(),
                item.getPrice(),
                item.getCurrency()
        );
    }
}