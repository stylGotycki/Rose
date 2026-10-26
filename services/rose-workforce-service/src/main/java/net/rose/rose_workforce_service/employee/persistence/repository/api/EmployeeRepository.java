package net.rose.rose_workforce_service.employee.persistence.repository.api;

import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.employee.domain.model.EmployeeStatus;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {


    List<EmployeeEntity> findAllByRestaurantIdAndStatus(
            RestaurantId restaurantId,
            EmployeeStatus status
    );


    Optional<EmployeeEntity> findByIdAndStatus(
            UUID id,
            EmployeeStatus status
    );


    boolean existsByRestaurantIdAndEmailAndStatus(
            RestaurantId restaurantId,
            String email,
            EmployeeStatus status
    );


    boolean existsByIdAndRestaurantIdAndStatus(
            UUID id,
            RestaurantId restaurantId,
            EmployeeStatus status
    );
}