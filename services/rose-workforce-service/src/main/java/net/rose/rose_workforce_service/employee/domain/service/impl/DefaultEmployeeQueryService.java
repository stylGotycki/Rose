package net.rose.rose_workforce_service.employee.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_workforce_service.core.exception.NotFoundException;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeStatus;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeQueryService;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;
import net.rose.rose_workforce_service.employee.persistence.repository.api.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultEmployeeQueryService implements EmployeeQueryService {

    private final EmployeeRepository repository;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    @Transactional(readOnly = true)
    public List<EmployeeEntity> findActiveByRestaurantId(
            RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return repository.findAllByRestaurantIdAndStatus(
                restaurantId,
                EmployeeStatus.ACTIVE
        );
    }


    @Override
    @Transactional(readOnly = true)
    public EmployeeEntity findActiveById(UUID employeeId) {
        EmployeeEntity employee = repository.findByIdAndStatus(
                        employeeId,
                        EmployeeStatus.ACTIVE
                )
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        restaurantAccessGuard.requireActiveAccess(employee.getRestaurantId());

        return employee;
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existsActiveByIdAndRestaurantId(
            UUID employeeId,
            RestaurantId restaurantId
    ) {
        return repository.existsByIdAndRestaurantIdAndStatus(
                employeeId,
                restaurantId,
                EmployeeStatus.ACTIVE
        );
    }
}