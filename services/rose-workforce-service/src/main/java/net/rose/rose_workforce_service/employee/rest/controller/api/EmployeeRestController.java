package net.rose.rose_workforce_service.employee.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_workforce_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeCreateDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeReadDto;
import net.rose.rose_workforce_service.employee.rest.dto.EmployeeUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api")
public interface EmployeeRestController {


    @PostMapping("/restaurants/{restaurantId}/employees")
    @ResponseStatus(HttpStatus.CREATED)
    EmployeeReadDto create(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid EmployeeCreateDto dto
    );


    @GetMapping("/restaurants/{restaurantId}/employees")
    CollectionRestDto<EmployeeReadDto> getAllByRestaurant(
            @PathVariable UUID restaurantId
    );


    @GetMapping("/employees/{employeeId}")
    EmployeeReadDto getById(
            @PathVariable UUID employeeId
    );


    @PutMapping("/employees/{employeeId}")
    EmployeeReadDto update(
            @PathVariable UUID employeeId,
            @RequestBody @Valid EmployeeUpdateDto dto
    );


    @DeleteMapping("/employees/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void archive(
            @PathVariable UUID employeeId
    );
}
