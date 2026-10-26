package net.rose.rose_organization_service.restaurant.domain.command;

import net.rose.rose_organization_service.core.persistence.id.UserId;

public record CreateRestaurantCommand(
        UserId ownerUserId,
        String name,
        String description,
        String taxId
) {
}