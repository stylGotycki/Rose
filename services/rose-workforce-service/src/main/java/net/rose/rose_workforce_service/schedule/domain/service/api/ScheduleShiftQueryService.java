package net.rose.rose_workforce_service.schedule.domain.service.api;

import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ScheduleShiftQueryService {

    List<ScheduleShiftEntity> findPlannedByRestaurantId(RestaurantId restaurantId);


    List<ScheduleShiftEntity> findByRestaurantIdAndPeriod(
            RestaurantId restaurantId,
            Instant from,
            Instant to
    );


    List<ScheduleShiftEntity> findPlannedByEmployeeId(EmployeeId employeeId);


    ScheduleShiftEntity findPlannedById(UUID shiftId);
}