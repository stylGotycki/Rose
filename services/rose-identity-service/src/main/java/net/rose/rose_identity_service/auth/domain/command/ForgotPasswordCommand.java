package net.rose.rose_identity_service.auth.domain.command;

public record ForgotPasswordCommand(
        String email
) {
}
