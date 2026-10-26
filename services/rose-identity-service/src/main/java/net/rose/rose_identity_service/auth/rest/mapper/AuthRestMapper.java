package net.rose.rose_identity_service.auth.rest.mapper;

import net.rose.rose_identity_service.auth.domain.command.*;
import net.rose.rose_identity_service.auth.rest.dto.*;
import org.springframework.stereotype.Component;

@Component
public class AuthRestMapper {

    public RegisterCommand toCommand(RegisterRequestDto dto) {
        return new RegisterCommand(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.password()
        );
    }


    public LoginCommand toCommand(LoginRequestDto dto) {
        return new LoginCommand(
                dto.email(),
                dto.password()
        );
    }


    public ForgotPasswordCommand toCommand(ForgotPasswordRequestDto dto) {
        return new ForgotPasswordCommand(
                dto.email()
        );
    }


    public ResetPasswordCommand toCommand(ResetPasswordRequestDto dto) {
        return new ResetPasswordCommand(
                dto.token(),
                dto.newPassword()
        );
    }


    public ChangePasswordCommand toCommand(ChangePasswordRequestDto dto) {
        return new ChangePasswordCommand(
                dto.oldPassword(),
                dto.newPassword()
        );
    }

}