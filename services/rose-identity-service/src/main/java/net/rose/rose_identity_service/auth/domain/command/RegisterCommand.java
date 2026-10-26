package net.rose.rose_identity_service.auth.domain.command;

public record RegisterCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}