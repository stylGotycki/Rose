package net.rose.rose_identity_service.user.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_identity_service.core.rest.dto.CollectionRestDto;
import net.rose.rose_identity_service.user.rest.dto.UserReadDto;
import net.rose.rose_identity_service.user.rest.dto.UserUpdateDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface UserRestController {

    @GetMapping(
            path = "/api/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserReadDto> getUser(
            @PathVariable UUID userId
    );

    @GetMapping(
            path = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CollectionRestDto<UserReadDto>> getUsers();

    @GetMapping(
            path = "/api/users/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<CollectionRestDto<UserReadDto>> searchUsers(
            @RequestParam(name = "email") String email
    );

    @PutMapping(
            path = "/api/users/{userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserUpdateDto dto
    );

    @PostMapping(
            path = "/api/users/{userId}/disable"
    )
    ResponseEntity<Void> disableUser(
            @PathVariable UUID userId
    );
}