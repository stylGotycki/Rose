package net.rose.rose_order_service.menu.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_order_service.menu.domain.service.api.MenuItemProjectionService;
import net.rose.rose_order_service.menu.integration.event.MenuItemChangedEvent;
import net.rose.rose_order_service.menu.persistence.entity.MenuItemProjectionEntity;
import net.rose.rose_order_service.menu.persistence.repository.api.MenuItemProjectionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultMenuItemProjectionService
        implements MenuItemProjectionService {

    private final MenuItemProjectionRepository repository;

    @Override
    public void apply(MenuItemChangedEvent event) {
        validate(event);

        Instant now = Instant.now();

        MenuItemProjectionEntity projection =
                repository.findById(event.menuItemId())
                        .orElseGet(() ->
                                MenuItemProjectionEntity.builder()
                                        .menuItemId(event.menuItemId())
                                        .restaurantId(event.restaurantId())
                                        .name(event.name())
                                        .price(event.price())
                                        .currency(event.currency())
                                        .orderable(event.orderable())
                                        .sourceChangedAt(event.changedAt())
                                        .updatedAt(now)
                                        .build()
                        );

        if (!projection.getRestaurantId()
                .equals(event.restaurantId())) {
            throw new IllegalStateException(
                    "Menu item restaurant cannot be changed"
            );
        }

        /*
         * Ignorujemy starszy event, który dotarł po nowszym.
         */
        if (projection.getSourceChangedAt() != null
                && projection.getSourceChangedAt()
                .isAfter(event.changedAt())) {
            return;
        }

        projection.update(
                event.name(),
                event.price(),
                event.currency(),
                event.orderable(),
                event.changedAt(),
                now
        );

        repository.save(projection);
    }

    private void validate(MenuItemChangedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Menu item event cannot be null"
            );
        }

        if (event.menuItemId() == null) {
            throw new IllegalArgumentException(
                    "Menu item id cannot be null"
            );
        }

        if (event.restaurantId() == null) {
            throw new IllegalArgumentException(
                    "Restaurant id cannot be null"
            );
        }

        if (event.name() == null || event.name().isBlank()) {
            throw new IllegalArgumentException(
                    "Menu item name cannot be blank"
            );
        }

        if (event.price() == null
                || event.price().signum() < 0) {
            throw new IllegalArgumentException(
                    "Menu item price cannot be negative"
            );
        }

        if (event.currency() == null
                || event.currency().isBlank()) {
            throw new IllegalArgumentException(
                    "Menu item currency cannot be blank"
            );
        }

        if (event.changedAt() == null) {
            throw new IllegalArgumentException(
                    "Changed at cannot be null"
            );
        }
    }
}