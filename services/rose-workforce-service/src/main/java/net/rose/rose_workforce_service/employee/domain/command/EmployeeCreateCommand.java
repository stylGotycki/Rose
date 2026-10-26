package net.rose.rose_workforce_service.employee.domain.command;

import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.core.persistence.id.UserId;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeRole;

public record EmployeeCreateCommand(
        RestaurantId restaurantId,
        UserId userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        EmployeeRole role
) {
}