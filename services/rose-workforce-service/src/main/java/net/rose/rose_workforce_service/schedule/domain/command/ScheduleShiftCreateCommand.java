package net.rose.rose_workforce_service.schedule.domain.command;

import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.schedule.domain.model.ShiftPosition;

import java.time.Instant;

public record ScheduleShiftCreateCommand(
        RestaurantId restaurantId,
        EmployeeId employeeId,
        Instant startsAt,
        Instant endsAt,
        ShiftPosition position,
        String note
) {
}