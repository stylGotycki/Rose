package net.rose.rose_identity_service.auth.domain.command;

public record ChangePasswordCommand(
        String oldPassword,
        String newPassword
) {
}
