package net.rose.rose_workforce_service.employee.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeCommandService;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeQueryService;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;
import net.rose.rose_workforce_service.employee.rest.controller.api.EmployeeRestController;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeCreateDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeReadDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeUpdateDto;
import net.rose.rose_workforce_service.employee.rest.mapper.EmployeeRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultEmployeeRestController implements EmployeeRestController {

    private final EmployeeCommandService commandService;


    private final EmployeeQueryService queryService;


    private final EmployeeRestMapper mapper;


    @Override
    public EmployeeReadDto create(UUID restaurantId, EmployeeCreateDto dto) {
        EmployeeEntity employee = commandService.create(
                mapper.toCommand(restaurantId, dto)
        );

        return mapper.toDto(employee);
    }


    @Override
    public CollectionRestDto<EmployeeReadDto> getAllByRestaurant(UUID restaurantId) {
        return new CollectionRestDto<>(
                queryService.findActiveByRestaurantId(RestaurantId.of(restaurantId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public EmployeeReadDto getById(UUID employeeId) {
        return mapper.toDto(
                queryService.findActiveById(employeeId)
        );
    }


    @Override
    public EmployeeReadDto update(UUID employeeId, EmployeeUpdateDto dto) {
        EmployeeEntity employee = commandService.update(
                employeeId,
                mapper.toCommand(dto)
        );

        return mapper.toDto(employee);
    }


    @Override
    public void archive(UUID employeeId) {
        commandService.archive(employeeId);
    }
}