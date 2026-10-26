package net.rose.rose_workforce_service.schedule.domain.command;

import net.rose.rose_workforce_service.schedule.domain.model.ShiftPosition;

import java.time.Instant;

public record ScheduleShiftUpdateCommand(
        Instant startsAt,
        Instant endsAt,
        ShiftPosition position,
        String note
) {
}