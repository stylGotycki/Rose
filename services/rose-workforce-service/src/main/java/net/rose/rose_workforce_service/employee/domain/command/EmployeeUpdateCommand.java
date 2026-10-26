package net.rose.rose_workforce_service.employee.domain.command;

import net.rose.rose_workforce_service.employee.domain.model.EmployeeRole;

public record EmployeeUpdateCommand(
        String firstName,
        String lastName,
        String email,
        String phone,
        EmployeeRole role
) {
}