package net.rose.rose_identity_service.user.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.core.exception.EntityAlreadyExistsException;
import net.rose.rose_identity_service.core.exception.NotFoundException;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.command.CreateUserCommand;
import net.rose.rose_identity_service.user.domain.command.UpdateUserCommand;
import net.rose.rose_identity_service.user.domain.event.UserCreatedEvent;
import net.rose.rose_identity_service.user.domain.event.UserStatusChangedEvent;
import net.rose.rose_identity_service.user.domain.event.UserUpdatedEvent;
import net.rose.rose_identity_service.user.domain.model.UserStatus;
import net.rose.rose_identity_service.user.domain.service.api.UserCommandService;
import net.rose.rose_identity_service.user.persistence.entity.UserEntity;
import net.rose.rose_identity_service.user.persistence.repository.api.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultUserCommandService implements UserCommandService {

    private final UserRepository repository;


    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public UserId createUser(CreateUserCommand command) {
        if (repository.existsByEmail(command.email())) {
            throw EntityAlreadyExistsException.of("User", "email", command.email());
        }

        UserEntity entity = UserEntity.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .status(UserStatus.PENDING_VERIFICATION)
                .build();

        UserEntity saved = repository.save(entity);

        eventPublisher.publishEvent(new UserCreatedEvent(
                UserId.of(saved.getId()),
                saved.getEmail()
        ));

        return UserId.of(saved.getId());
    }


    @Override
    @Transactional
    public void verifyUser(UserId userId) {
        UserEntity entity = getEntityById(userId);

        entity.setStatus(UserStatus.ACTIVE);
        entity.setVerifiedAt(Instant.now());

        eventPublisher.publishEvent(new UserStatusChangedEvent(
                UserId.of(entity.getId()),
                UserStatus.ACTIVE
        ));
    }


    @Override
    @Transactional
    public void disableUser(UserId userId) {
        UserEntity entity = getEntityById(userId);

        entity.setStatus(UserStatus.DISABLED);
        entity.setDisabledAt(Instant.now());

        eventPublisher.publishEvent(new UserStatusChangedEvent(
                UserId.of(entity.getId()),
                UserStatus.DISABLED
        ));
    }


    @Override
    @Transactional
    public void updateUser(UserId userId, UpdateUserCommand command) {
        UserEntity entity = getEntityById(userId);

        entity.setFirstName(command.firstName());
        entity.setLastName(command.lastName());

        eventPublisher.publishEvent(new UserUpdatedEvent(UserId.of(entity.getId())));
    }


    private UserEntity getEntityById(UserId userId) {
        return repository.findById(userId.getId())
                .orElseThrow(() -> NotFoundException.of("User", userId));
    }
}