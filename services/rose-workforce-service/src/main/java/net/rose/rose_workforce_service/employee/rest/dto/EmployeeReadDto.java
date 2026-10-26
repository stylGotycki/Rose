package net.rose.rose_workforce_service.employee.rest.dto;

import net.rose.rose_workforce_service.employee.domain.model.EmployeeRole;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeStatus;

import java.time.Instant;
import java.util.UUID;

public record EmployeeReadDto(

        UUID id,
        UUID restaurantId,
        UUID userId,
        String firstName,
        String lastName,
        String email,
        String phone,
        EmployeeRole role,
        EmployeeStatus status,
        Instant createdAt,
        Instant updatedAt

) {
}
