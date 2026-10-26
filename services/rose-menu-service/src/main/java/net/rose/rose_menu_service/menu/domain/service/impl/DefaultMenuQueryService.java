package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.core.persistence.id.RestaurantId;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuQueryService;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultMenuQueryService implements MenuQueryService {

    private final MenuRepository repository;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    @Transactional(readOnly = true)
    public List<MenuEntity> findAllByRestaurantId(
            RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return repository
                .findAllByRestaurantIdAndStatusNotOrderByCreatedAtDesc(
                        restaurantId,
                        MenuStatus.ARCHIVED
                );
    }


    @Override
    @Transactional(readOnly = true)
    public MenuEntity findVisibleById(UUID menuId) {
        MenuEntity menu = repository.findByIdAndStatusNot(
                        menuId,
                        MenuStatus.ARCHIVED
                )
                .orElseThrow(() -> new NotFoundException("Menu not found"));

        restaurantAccessGuard.requireActiveAccess(menu.getRestaurantId());

        return menu;
    }
}