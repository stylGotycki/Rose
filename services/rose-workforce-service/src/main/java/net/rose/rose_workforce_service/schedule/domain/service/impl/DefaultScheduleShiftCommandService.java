package net.rose.rose_workforce_service.schedule.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.access.domain.model.RestaurantMemberRole;
import net.rose.rose_workforce_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_workforce_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_workforce_service.core.exception.NotFoundException;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeQueryService;
import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftCreateCommand;
import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftUpdateCommand;
import net.rose.rose_workforce_service.schedule.domain.model.ScheduleShiftStatus;
import net.rose.rose_workforce_service.schedule.domain.service.api.ScheduleShiftCommandService;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;
import net.rose.rose_workforce_service.schedule.persistence.repository.api.ScheduleShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultScheduleShiftCommandService implements ScheduleShiftCommandService {

    private final ScheduleShiftRepository repository;


    private final EmployeeQueryService employeeQueryService;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    @Transactional
    public ScheduleShiftEntity create(
            ScheduleShiftCreateCommand command
    ) {
        restaurantAccessGuard.requireAnyRole(
                command.restaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        validateTimeRange(command.startsAt(), command.endsAt());

        boolean employeeExists =
                employeeQueryService.existsActiveByIdAndRestaurantId(
                        command.employeeId().getId(),
                        command.restaurantId()
                );

        if (!employeeExists) {
            throw new NotFoundException(
                    "Employee not found in this restaurant"
            );
        }

        ScheduleShiftEntity shift = ScheduleShiftEntity.builder()
                .restaurantId(command.restaurantId())
                .employeeId(command.employeeId())
                .startsAt(command.startsAt())
                .endsAt(command.endsAt())
                .position(command.position())
                .status(ScheduleShiftStatus.PLANNED)
                .note(command.note())
                .build();

        return repository.save(shift);
    }


    @Override
    @Transactional
    public ScheduleShiftEntity update(
            UUID shiftId,
            ScheduleShiftUpdateCommand command
    ) {
        validateTimeRange(command.startsAt(), command.endsAt());

        ScheduleShiftEntity shift = findPlanned(shiftId);

        restaurantAccessGuard.requireAnyRole(
                shift.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        shift.setStartsAt(command.startsAt());
        shift.setEndsAt(command.endsAt());
        shift.setPosition(command.position());
        shift.setNote(command.note());

        return repository.save(shift);
    }


    @Override
    @Transactional
    public void cancel(UUID shiftId) {
        ScheduleShiftEntity shift = findPlanned(shiftId);

        restaurantAccessGuard.requireAnyRole(
                shift.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        shift.cancel();
        repository.save(shift);
    }


    @Override
    @Transactional
    public void complete(UUID shiftId) {
        ScheduleShiftEntity shift = findPlanned(shiftId);

        restaurantAccessGuard.requireAnyRole(
                shift.getRestaurantId(),
                RestaurantMemberRole.OWNER,
                RestaurantMemberRole.MANAGER
        );

        shift.complete();
        repository.save(shift);
    }


    private ScheduleShiftEntity findPlanned(UUID shiftId) {
        return repository.findByIdAndStatus(
                        shiftId,
                        ScheduleShiftStatus.PLANNED
                )
                .orElseThrow(() ->
                        new NotFoundException("Schedule shift not found")
                );
    }


    private void validateTimeRange(
            Instant startsAt,
            Instant endsAt
    ) {
        if (startsAt == null || endsAt == null) {
            throw new BusinessRuleViolationException(
                    "Shift start and end time are required"
            );
        }

        if (!startsAt.isBefore(endsAt)) {
            throw new BusinessRuleViolationException(
                    "Shift start time must be before end time"
            );
        }
    }
}