package net.rose.rose_identity_service.user.domain.command;

import lombok.Builder;

@Builder
public record UpdateUserCommand(
        String firstName,
        String lastName
) {
}
