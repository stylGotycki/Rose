package net.rose.rose_identity_service.user.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.core.exception.NotFoundException;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.model.UserProfile;
import net.rose.rose_identity_service.user.domain.model.UserStatus;
import net.rose.rose_identity_service.user.domain.service.api.UserQueryService;
import net.rose.rose_identity_service.user.persistence.entity.UserEntity;
import net.rose.rose_identity_service.user.persistence.repository.api.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
class DefaultUserQueryService implements UserQueryService {

    private final UserRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<UserProfile> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toProfile)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfile> searchActiveByEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }

        return repository
                .findTop10ByStatusAndEmailContainingIgnoreCaseOrderByEmailAsc(
                        UserStatus.ACTIVE,
                        email.trim()
                )
                .stream()
                .map(this::toProfile)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getById(UserId userId) {
        return repository.findById(userId.getId())
                .map(this::toProfile)
                .orElseThrow(() ->
                        NotFoundException.of("User", userId)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfile getByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::toProfile)
                .orElseThrow(() ->
                        NotFoundException.of(
                                "User with email",
                                email
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UserId userId) {
        return repository.existsById(userId.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private UserProfile toProfile(UserEntity entity) {
        return new UserProfile(
                UserId.of(entity.getId()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getStatus()
        );
    }
}