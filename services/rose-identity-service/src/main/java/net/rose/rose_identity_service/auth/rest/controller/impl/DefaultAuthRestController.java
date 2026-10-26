package net.rose.rose_identity_service.auth.rest.controller.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.auth.domain.service.api.AuthService;
import net.rose.rose_identity_service.auth.rest.controller.api.AuthRestController;
import net.rose.rose_identity_service.auth.rest.dto.*;
import net.rose.rose_identity_service.auth.rest.mapper.AuthRestMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DefaultAuthRestController implements AuthRestController {

    private final AuthService authService;


    private final AuthRestMapper mapper;


    @Override
    public ResponseEntity<Void> register(RegisterRequestDto dto) {
        authService.register(mapper.toCommand(dto));

        return ResponseEntity.accepted().build();
    }


    @Override
    public ResponseEntity<Void> verify(String token) {
        authService.verify(token);

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<TokenPairDto> login(LoginRequestDto dto) {
        TokenPairDto tokenPair = authService.login(mapper.toCommand(dto));

        return ResponseEntity.ok(tokenPair);
    }



    @Override
    public ResponseEntity<SessionDto> session() {
        SessionDto session = authService.session();

        return ResponseEntity.ok(session);
    }


    @Override
    public ResponseEntity<UserMeDto> me() {
        UserMeDto me = authService.me();

        return ResponseEntity.ok(me);
    }


    @Override
    public ResponseEntity<Void> logout(String authorizationHeader, RefreshRequestDto dto) {
        String accessToken = extractBearerToken(authorizationHeader);

        authService.logout(accessToken, dto.refreshToken());

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<TokenPairDto> refresh(RefreshRequestDto dto) {
        TokenPairDto tokenPair = authService.refresh(dto.refreshToken());

        return ResponseEntity.ok(tokenPair);
    }


    @Override
    public ResponseEntity<Void> forgotPassword(ForgotPasswordRequestDto dto) {
        authService.forgotPassword(mapper.toCommand(dto));

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordRequestDto dto) {
        authService.resetPassword(mapper.toCommand(dto));

        return ResponseEntity.noContent().build();
    }


    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequestDto dto) {
        authService.changePassword(mapper.toCommand(dto));

        return ResponseEntity.noContent().build();
    }


    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }

        return authorizationHeader.substring("Bearer ".length());
    }

}