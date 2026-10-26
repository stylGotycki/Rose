package net.rose.rose_workforce_service.schedule.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_workforce_service.access.domain.service.api.RestaurantAccessGuard;
import net.rose.rose_workforce_service.core.exception.BusinessRuleViolationException;
import net.rose.rose_workforce_service.core.exception.NotFoundException;
import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.employee.domain.service.api.EmployeeQueryService;
import net.rose.rose_workforce_service.schedule.domain.model.ScheduleShiftStatus;
import net.rose.rose_workforce_service.schedule.domain.service.api.ScheduleShiftQueryService;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;
import net.rose.rose_workforce_service.schedule.persistence.repository.api.ScheduleShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class DefaultScheduleShiftQueryService implements ScheduleShiftQueryService {

    private final ScheduleShiftRepository repository;


    private final EmployeeQueryService employeeQueryService;


    private final RestaurantAccessGuard restaurantAccessGuard;


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleShiftEntity> findPlannedByRestaurantId(
            RestaurantId restaurantId
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);

        return repository
                .findAllByRestaurantIdAndStatusOrderByStartsAtAsc(
                        restaurantId,
                        ScheduleShiftStatus.PLANNED
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleShiftEntity> findByRestaurantIdAndPeriod(
            RestaurantId restaurantId,
            Instant from,
            Instant to
    ) {
        restaurantAccessGuard.requireActiveAccess(restaurantId);
        validatePeriod(from, to);

        return repository
                .findAllByRestaurantIdAndStartsAtBetweenOrderByStartsAtAsc(
                        restaurantId,
                        from,
                        to
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<ScheduleShiftEntity> findPlannedByEmployeeId(
            EmployeeId employeeId
    ) {

        employeeQueryService.findActiveById(employeeId.getId());

        return repository
                .findAllByEmployeeIdAndStatusOrderByStartsAtAsc(
                        employeeId,
                        ScheduleShiftStatus.PLANNED
                );
    }


    @Override
    @Transactional(readOnly = true)
    public ScheduleShiftEntity findPlannedById(UUID shiftId) {
        ScheduleShiftEntity shift = repository.findByIdAndStatus(
                        shiftId,
                        ScheduleShiftStatus.PLANNED
                )
                .orElseThrow(() ->
                        new NotFoundException("Schedule shift not found")
                );

        restaurantAccessGuard.requireActiveAccess(shift.getRestaurantId());

        return shift;
    }


    private void validatePeriod(Instant from, Instant to) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new BusinessRuleViolationException(
                    "Invalid schedule period"
            );
        }
    }
}