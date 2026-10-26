package net.rose.rose_organization_service.restaurant.domain.command;


public record UpdateRestaurantCommand(
        String name,
        String description,
        String taxId
) {
}