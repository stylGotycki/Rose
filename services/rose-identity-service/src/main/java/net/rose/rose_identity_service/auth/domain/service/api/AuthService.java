package net.rose.rose_identity_service.auth.domain.service.api;

import net.rose.rose_identity_service.auth.domain.command.*;
import net.rose.rose_identity_service.auth.rest.dto.SessionDto;
import net.rose.rose_identity_service.auth.rest.dto.TokenPairDto;
import net.rose.rose_identity_service.auth.rest.dto.UserMeDto;

public interface AuthService {

    void register(RegisterCommand command);


    void verify(String token);


    TokenPairDto login(LoginCommand command);


    TokenPairDto refresh(String refreshToken);


    SessionDto session();


    UserMeDto me();


    void logout(String accessToken, String refreshToken);


    void forgotPassword(ForgotPasswordCommand command);


    void resetPassword(ResetPasswordCommand command);


    void changePassword(ChangePasswordCommand command);

}