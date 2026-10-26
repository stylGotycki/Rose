package net.rose.rose_workforce_service.schedule.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.rose.rose_workforce_service.schedule.domain.model.ShiftPosition;

import java.time.Instant;

public record ScheduleShiftUpdateDto(
        @NotNull
        Instant startsAt,

        @NotNull
        Instant endsAt,

        @NotNull
        ShiftPosition position,

        @Size(max = 1000)
        String note
) {
}
