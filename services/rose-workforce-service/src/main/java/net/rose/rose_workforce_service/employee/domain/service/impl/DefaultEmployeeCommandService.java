package net.rose.rose_workforce_service.employee.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_workforce_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_workforce_service.core.exception.EntityAlreadyExistsException;
import net.rose.rose_workforce_service.core.exception.NotFoundException;
import net.rose.rose_workforce_service.employee.domain.command.EmployeeCreateCommand;
import net.rose.rose_workforce_service.employee.domain.command.EmployeeUpdateCommand;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeStatus;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeCommandService;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;
import net.rose.rose_workforce_service.employee.persistence.repository.api.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultEmployeeCommandService implements EmployeeCommandService {

    private final EmployeeRepository repository;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    @Transactional
    public EmployeeEntity create(EmployeeCreateCommand command) {
        restaurantAccessGuard.requireAnyRole(
                command.restaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        boolean employeeExists =
                repository.existsByRestaurantIdAndEmailAndStatus(
                        command.restaurantId(),
                        command.email(),
                        EmployeeStatus.ACTIVE
                );

        if (employeeExists) {
            throw new EntityAlreadyExistsException(
                    "Employee with this email already exists in this restaurant"
            );
        }

        EmployeeEntity employee = EmployeeEntity.builder()
                .restaurantId(command.restaurantId())
                .userId(command.userId())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .phone(command.phone())
                .role(command.role())
                .status(EmployeeStatus.ACTIVE)
                .build();

        return repository.save(employee);
    }


    @Override
    @Transactional
    public EmployeeEntity update(
            UUID employeeId,
            EmployeeUpdateCommand command
    ) {
        EmployeeEntity employee = findActive(employeeId);

        restaurantAccessGuard.requireAnyRole(
                employee.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        employee.setFirstName(command.firstName());
        employee.setLastName(command.lastName());
        employee.setEmail(command.email());
        employee.setPhone(command.phone());
        employee.setRole(command.role());

        return repository.save(employee);
    }


    @Override
    @Transactional
    public void archive(UUID employeeId) {
        EmployeeEntity employee = findActive(employeeId);

        restaurantAccessGuard.requireAnyRole(
                employee.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        employee.archive();
        repository.save(employee);
    }


    private EmployeeEntity findActive(UUID employeeId) {
        return repository.findByIdAndStatus(
                        employeeId,
                        EmployeeStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }
}