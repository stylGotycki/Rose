package net.rose.rose_workforce_service.schedule.domain.service.api;

import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftCreateCommand;
import net.rose.rose_workforce_service.schedule.domain.command.ScheduleShiftUpdateCommand;
import net.rose.rose_workforce_service.schedule.persistence.entity.ScheduleShiftEntity;

import java.util.UUID;

public interface ScheduleShiftCommandService {

    ScheduleShiftEntity create(ScheduleShiftCreateCommand command);


    ScheduleShiftEntity update(UUID shiftId, ScheduleShiftUpdateCommand command);


    void cancel(UUID shiftId);


    void complete(UUID shiftId);

}