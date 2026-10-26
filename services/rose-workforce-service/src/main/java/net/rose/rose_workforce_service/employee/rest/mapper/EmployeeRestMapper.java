package net.rose.rose_workforce_service.employee.rest.mapper;

import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.core.persistence.id.UserId;
import net.rose.rose_workforce_service.employee.domain.command.EmployeeCreateCommand;
import net.rose.rose_workforce_service.employee.domain.command.EmployeeUpdateCommand;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeCreateDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeReadDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeUpdateDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmployeeRestMapper {

    public EmployeeCreateCommand toCommand(UUID restaurantId, EmployeeCreateDto dto) {
        return new EmployeeCreateCommand(
                RestaurantId.of(restaurantId),
                dto.userId() == null ? null : UserId.of(dto.userId()),
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.phone(),
                dto.role()
        );
    }


    public EmployeeUpdateCommand toCommand(EmployeeUpdateDto dto) {
        return new EmployeeUpdateCommand(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.phone(),
                dto.role()
        );
    }


    public EmployeeReadDto toDto(EmployeeEntity employee) {
        return new EmployeeReadDto(
                employee.getId(),
                employee.getRestaurantId().getId(),
                employee.getUserId() == null ? null : employee.getUserId().getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getRole(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}