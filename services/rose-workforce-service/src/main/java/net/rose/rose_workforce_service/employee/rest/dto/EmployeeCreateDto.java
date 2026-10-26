package net.rose.rose_workforce_service.employee.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeRole;

import java.util.UUID;

public record EmployeeCreateDto(

        UUID userId,

        @NotBlank
        @Size(max = 64)
        String firstName,

        @NotBlank
        @Size(max = 64)
        String lastName,

        @NotBlank
        @Email
        @Size(max = 128)
        String email,

        @Size(max = 32)
        String phone,

        @NotNull
        EmployeeRole role

) {
}
