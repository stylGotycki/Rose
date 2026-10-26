package net.rose.rose_menu_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_menu_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_menu_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_menu_service.core.exception.EntityAlreadyExistsException;
import net.rose.rose_menu_service.core.exception.NotFoundException;
import net.rose.rose_menu_service.menu.domain.command.MenuCreateCommand;
import net.rose.rose_menu_service.menu.domain.command.MenuUpdateCommand;
import net.rose.rose_menu_service.menu.domain.model.MenuStatus;
import net.rose.rose_menu_service.menu.domain.service.api.MenuCommandService;
import net.rose.rose_menu_service.menu.integration.publisher.MenuItemChangedPublisher;
import net.rose.rose_menu_service.menu.persistence.entity.MenuCategoryEntity;
import net.rose.rose_menu_service.menu.persistence.entity.MenuEntity;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuCategoryRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuItemRepository;
import net.rose.rose_menu_service.menu.persistence.repository.api.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class DefaultMenuCommandService implements MenuCommandService {

    private final MenuRepository repository;


    private final RestaurantAccessGuard restaurantAccessGuard;


    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;
    private final MenuItemChangedPublisher itemChangedPublisher;

    @Override
    @Transactional
    public MenuEntity create(MenuCreateCommand command) {
        requireMenuManagement(command.restaurantId());

        boolean exists =
                repository.existsByRestaurantIdAndNameAndStatusNot(
                        command.restaurantId(),
                        command.name(),
                        MenuStatus.ARCHIVED
                );

        if (exists) {
            throw new EntityAlreadyExistsException(
                    "Menu with this name already exists in this restaurant"
            );
        }

        MenuEntity menu = MenuEntity.builder()
                .restaurantId(command.restaurantId())
                .name(command.name())
                .description(command.description())
                .status(MenuStatus.DRAFT)
                .build();

        return repository.save(menu);
    }


    @Override
    @Transactional
    public MenuEntity update(
            UUID menuId,
            MenuUpdateCommand command
    ) {
        MenuEntity menu = findVisible(menuId);

        requireMenuManagement(menu.getRestaurantId());

        menu.setName(command.name());
        menu.setDescription(command.description());

        return repository.save(menu);
    }


    @Override
    @Transactional
    public void archive(UUID menuId) {
        MenuEntity menu = findVisible(menuId);

        requireMenuManagement(menu.getRestaurantId());

        menu.archive();

        MenuEntity savedMenu =
                repository.save(menu);

        publishAllMenuItems(savedMenu);
    }


    @Override
    @Transactional
    public MenuEntity activate(UUID menuId) {
        MenuEntity menu = findVisible(menuId);

        requireMenuManagement(menu.getRestaurantId());

        menu.activate();

        MenuEntity savedMenu =
                repository.save(menu);

        publishAllMenuItems(savedMenu);

        return savedMenu;
    }


    private MenuEntity findVisible(UUID menuId) {
        return repository.findByIdAndStatusNot(
                        menuId,
                        MenuStatus.ARCHIVED
                )
                .orElseThrow(() -> new NotFoundException("Menu not found"));
    }


    private void requireMenuManagement(
            net.rose.rose_menu_service.core.persistence.id.RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireAnyRole(
                restaurantId,
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER,
                RestaurantMemberRole.CHEF
        );
    }

    private void publishAllMenuItems(MenuEntity menu) {
        Map<UUID, MenuCategoryEntity> categories =
                categoryRepository
                        .findAllByMenuId(
                                net.rose.rose_menu_service.core.persistence.id.MenuId.of(
                                        menu.getId()
                                )
                        )
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        MenuCategoryEntity::getId,
                                        Function.identity()
                                )
                        );

        itemRepository.findAllByMenuId(menu.getId())
                .forEach(item -> {
                    MenuCategoryEntity category =
                            categories.get(
                                    item.getCategoryId().getId()
                            );

                    if (category == null) {
                        throw new IllegalStateException(
                                "Menu category not found for item "
                                        + item.getId()
                        );
                    }

                    itemChangedPublisher.publish(
                            item,
                            category,
                            menu
                    );
                });
    }
}