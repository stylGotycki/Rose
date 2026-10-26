package net.rose.rose_identity_service.auth.domain.command;

public record LoginCommand(
        String email,
        String password
) {
}