package net.rose.rose_workforce_service.schedule.rest.dto;

import net.rose.rose_workforce_service.schedule.domain.model.ScheduleShiftStatus;
import net.rose.rose_workforce_service.schedule.domain.model.ShiftPosition;

import java.time.Instant;
import java.util.UUID;

public record ScheduleShiftReadDto(
        UUID id,
        UUID restaurantId,
        UUID employeeId,
        Instant startsAt,
        Instant endsAt,
        ShiftPosition position,
        ScheduleShiftStatus status,
        String note,
        Instant createdAt,
        Instant updatedAt
) {
}
