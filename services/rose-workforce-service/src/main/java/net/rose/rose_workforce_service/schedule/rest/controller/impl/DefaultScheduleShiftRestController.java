package net.rose.rose_workforce_service.schedule.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_workforce_service.schedule.domain.service.api.ScheduleShiftCommandService;
import net.rose.rose_workforce_service.schedule.domain.service.api.ScheduleShiftQueryService;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;
import net.rose.rose_workforce_service.schedule.rest.controller.api.ScheduleShiftRestController;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftCreateDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftReadDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftUpdateDto;
import net.rose.rose_workforce_service.schedule.rest.mapper.ScheduleShiftRestMapper;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultScheduleShiftRestController implements ScheduleShiftRestController {

    private final ScheduleShiftCommandService commandService;


    private final ScheduleShiftQueryService queryService;


    private final ScheduleShiftRestMapper mapper;


    @Override
    public ScheduleShiftReadDto create(UUID restaurantId, ScheduleShiftCreateDto dto) {
        ScheduleShiftEntity shift = commandService.create(
                mapper.toCommand(restaurantId, dto)
        );

        return mapper.toDto(shift);
    }


    @Override
    public CollectionRestDto<ScheduleShiftReadDto> getPlannedByRestaurant(UUID restaurantId) {
        return new CollectionRestDto<>(
                queryService.findPlannedByRestaurantId(RestaurantId.of(restaurantId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public CollectionRestDto<ScheduleShiftReadDto> getByRestaurantAndPeriod(
            UUID restaurantId,
            Instant from,
            Instant to
    ) {
        return new CollectionRestDto<>(
                queryService.findByRestaurantIdAndPeriod(
                                RestaurantId.of(restaurantId),
                                from,
                                to
                        )
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public CollectionRestDto<ScheduleShiftReadDto> getPlannedByEmployee(UUID employeeId) {
        return new CollectionRestDto<>(
                queryService.findPlannedByEmployeeId(EmployeeId.of(employeeId))
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }


    @Override
    public ScheduleShiftReadDto getById(UUID shiftId) {
        return mapper.toDto(
                queryService.findPlannedById(shiftId)
        );
    }


    @Override
    public ScheduleShiftReadDto update(UUID shiftId, ScheduleShiftUpdateDto dto) {
        ScheduleShiftEntity shift = commandService.update(
                shiftId,
                mapper.toCommand(dto)
        );

        return mapper.toDto(shift);
    }


    @Override
    public void cancel(UUID shiftId) {
        commandService.cancel(shiftId);
    }


    @Override
    public void complete(UUID shiftId) {
        commandService.complete(shiftId);
    }
}