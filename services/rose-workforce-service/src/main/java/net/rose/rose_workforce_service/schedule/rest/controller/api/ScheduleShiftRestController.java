package net.rose.rose_workforce_service.schedule.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_workforce_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftCreateDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftReadDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RequestMapping("/api")
public interface ScheduleShiftRestController {

    @PostMapping("/restaurants/{restaurantId}/schedule-shifts")
    @ResponseStatus(HttpStatus.CREATED)
    ScheduleShiftReadDto create(
            @PathVariable UUID restaurantId,
            @RequestBody @Valid ScheduleShiftCreateDto dto
    );


    @GetMapping("/restaurants/{restaurantId}/schedule-shifts")
    CollectionRestDto<ScheduleShiftReadDto> getPlannedByRestaurant(
            @PathVariable UUID restaurantId
    );


    @GetMapping("/restaurants/{restaurantId}/schedule-shifts/period")
    CollectionRestDto<ScheduleShiftReadDto> getByRestaurantAndPeriod(
            @PathVariable UUID restaurantId,
            @RequestParam Instant from,
            @RequestParam Instant to
    );


    @GetMapping("/employees/{employeeId}/schedule-shifts")
    CollectionRestDto<ScheduleShiftReadDto> getPlannedByEmployee(
            @PathVariable UUID employeeId
    );


    @GetMapping("/schedule-shifts/{shiftId}")
    ScheduleShiftReadDto getById(
            @PathVariable UUID shiftId
    );


    @PutMapping("/schedule-shifts/{shiftId}")
    ScheduleShiftReadDto update(
            @PathVariable UUID shiftId,
            @RequestBody @Valid ScheduleShiftUpdateDto dto
    );


    @PatchMapping("/schedule-shifts/{shiftId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(
            @PathVariable UUID shiftId
    );


    @PatchMapping("/schedule-shifts/{shiftId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void complete(
            @PathVariable UUID shiftId
    );
}