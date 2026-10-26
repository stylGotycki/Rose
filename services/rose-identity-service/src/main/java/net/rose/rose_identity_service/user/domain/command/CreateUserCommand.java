package net.rose.rose_identity_service.user.domain.command;

import lombok.Builder;

@Builder
public record CreateUserCommand(
        String firstName,
        String lastName,
        String email
) {
}
