package net.rose.rose_workforce_service.schedule.persistence.repository.api;

import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.schedule.domain.model.ScheduleShiftStatus;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleShiftRepository extends JpaRepository<ScheduleShiftEntity, UUID> {

    List<ScheduleShiftEntity> findAllByRestaurantIdAndStatusOrderByStartsAtAsc(
            RestaurantId restaurantId,
            ScheduleShiftStatus status
    );


    List<ScheduleShiftEntity> findAllByRestaurantIdAndStartsAtBetweenOrderByStartsAtAsc(
            RestaurantId restaurantId,
            Instant from,
            Instant to
    );


    List<ScheduleShiftEntity> findAllByEmployeeIdAndStatusOrderByStartsAtAsc(
            EmployeeId employeeId,
            ScheduleShiftStatus status
    );


    Optional<ScheduleShiftEntity> findByIdAndStatus(
            UUID id,
            ScheduleShiftStatus status
    );

}