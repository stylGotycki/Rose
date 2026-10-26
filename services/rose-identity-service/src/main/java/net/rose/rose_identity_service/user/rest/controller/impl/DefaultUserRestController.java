package net.rose.rose_identity_service.user.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_identity_service.user.domain.model.UserProfile;
import net.rose.rose_identity_service.user.domain.service.api.UserCommandService;
import net.rose.rose_identity_service.user.domain.service.api.UserQueryService;
import net.rose.rose_identity_service.user.rest.controller.api.UserRestController;
import net.rose.rose_identity_service.user.rest.dto.UserReadDto;
import net.rose.rose_identity_service.user.rest.dto.UserUpdateDto;
import net.rose.rose_identity_service.user.rest.mapper.UserRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class DefaultUserRestController implements UserRestController {

    private final UserCommandService commandService;

    private final UserQueryService queryService;

    private final UserRestMapper mapper;

    @Override
    public ResponseEntity<CollectionRestDto<UserReadDto>> getUsers() {
        List<UserProfile> profiles = queryService.getAll();

        return ResponseEntity.ok(
                mapper.toDto(profiles)
        );
    }

    @Override
    public ResponseEntity<CollectionRestDto<UserReadDto>> searchUsers(
            String email
    ) {
        List<UserProfile> profiles =
                queryService.searchActiveByEmail(email);

        return ResponseEntity.ok(
                mapper.toDto(profiles)
        );
    }

    @Override
    public ResponseEntity<UserReadDto> getUser(UUID userId) {
        UserProfile profile =
                queryService.getById(UserId.of(userId));

        return ResponseEntity.ok(
                mapper.toDto(profile)
        );
    }

    @Override
    public ResponseEntity<Void> updateUser(
            UUID userId,
            UserUpdateDto dto
    ) {
        commandService.updateUser(
                UserId.of(userId),
                mapper.toCommand(dto)
        );

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> disableUser(UUID userId) {
        commandService.disableUser(
                UserId.of(userId)
        );

        return ResponseEntity.noContent().build();
    }
}