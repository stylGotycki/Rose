package net.rose.rose_identity_service.auth.domain.command;

public record ResetPasswordCommand(
        String token,
        String newPassword
) {
}
