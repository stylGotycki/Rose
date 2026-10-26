package net.rose.rose_workforce_service.employee.domain.service.api;

import net.rose.rose_workforce_service.employee.domain.command.EmployeeCreateCommand;
import net.rose.rose_workforce_service.employee.domain.command.EmployeeUpdateCommand;
import net.rose.rose_workforce_service.employee.persistence.entity.EmployeeEntity;

import java.util.UUID;

public interface EmployeeCommandService {


    EmployeeEntity create(EmployeeCreateCommand command);


    EmployeeEntity update(UUID employeeId, EmployeeUpdateCommand command);


    void archive(UUID employeeId);

}