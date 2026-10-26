package net.rose.rose_workforce_service.schedule.rest.mapper;

import net.rose.rose_workforce_service.core.persistence.id.EmployeeId;
import net.rose.rose_workforce_service.core.persistence.id.RestaurantId;
import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftCreateCommand;
import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftUpdateCommand;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftCreateDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftReadDto;
import net.rose.rose_workforce_service.schedule.rest.dto.ScheduleShiftUpdateDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScheduleShiftRestMapper {

    public ScheduleShiftCreateCommand toCommand(UUID restaurantId, ScheduleShiftCreateDto dto) {
        return new ScheduleShiftCreateCommand(
                RestaurantId.of(restaurantId),
                EmployeeId.of(dto.employeeId()),
                dto.startsAt(),
                dto.endsAt(),
                dto.position(),
                dto.note()
        );
    }

    public ScheduleShiftUpdateCommand toCommand(ScheduleShiftUpdateDto dto) {
        return new ScheduleShiftUpdateCommand(
                dto.startsAt(),
                dto.endsAt(),
                dto.position(),
                dto.note()
        );
    }

    public ScheduleShiftReadDto toDto(ScheduleShiftEntity entity) {
        return new ScheduleShiftReadDto(
                entity.getId(),
                entity.getRestaurantId().getId(),
                entity.getEmployeeId().getId(),
                entity.getStartsAt(),
                entity.getEndsAt(),
                entity.getPosition(),
                entity.getStatus(),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}