package net.rose.rose_identity_service.auth.rest.controller.api;

import jakarta.validation.Valid;
import net.rose.rose_identity_service.auth.rest.dto.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface AuthRestController {

    @PostMapping(
            path = "/api/auth/register",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> register(
            @RequestBody @Valid RegisterRequestDto dto
    );


    @GetMapping("/api/auth/verify")
    ResponseEntity<Void> verify(
            @RequestParam String token
    );


    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<TokenPairDto> login(
            @RequestBody @Valid LoginRequestDto dto
    );


    @PostMapping(
            path = "/api/auth/logout",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> logout(
            @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody RefreshRequestDto dto
    );


    @PostMapping(
            path = "/api/auth/refresh",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<TokenPairDto> refresh(
            @RequestBody @Valid RefreshRequestDto dto
    );


    @GetMapping(
            path = "/api/auth/session",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<SessionDto> session();


    @GetMapping(
            path = "/api/auth/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<UserMeDto> me();


    @PostMapping(
            path = "/api/auth/password/forgot",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> forgotPassword(
            @RequestBody @Valid ForgotPasswordRequestDto dto
    );


    @PostMapping(
            path = "/api/auth/password/reset",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequestDto dto
    );


    @PostMapping(
            path = "/api/auth/password/change",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<Void> changePassword(
            @RequestBody @Valid ChangePasswordRequestDto dto
    );

}
