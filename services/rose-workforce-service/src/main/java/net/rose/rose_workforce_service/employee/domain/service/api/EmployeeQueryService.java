package net.rose.rose_workforce_service.employee.domain.service.api;

import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;

import java.util.List;
import java.util.UUID;

public interface EmployeeQueryService {

    List<EmployeeEntity> findActiveByRestaurantId(RestaurantId restaurantId);


    EmployeeEntity findActiveById(UUID employeeId);


    boolean existsActiveByIdAndRestaurantId(UUID employeeId, RestaurantId restaurantId);

}