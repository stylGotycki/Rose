package net.rose.rose_identity_service.user.domain.service.api;

import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.command.CreateUserCommand;
import net.rose.rose_identity_service.user.domain.command.UpdateUserCommand;


public interface UserCommandService {

    UserId createUser(CreateUserCommand command);


    void verifyUser(UserId userId);


    void disableUser(UserId userId);


    void updateUser(UserId userId, UpdateUserCommand command);

}
