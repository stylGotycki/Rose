package net.rose.rose_identity_service.auth.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.auth.domain.command.*;
import net.rose.rose_identity_service.auth.domain.event.PasswordResetTokenCreatedEvent;
import net.rose.rose_identity_service.auth.domain.event.VerificationTokenCreatedEvent;
import net.rose.rose_identity_service.auth.domain.service.api.AuthService;
import net.rose.rose_identity_service.auth.domain.service.api.JwtService;
import net.rose.rose_identity_service.auth.domain.service.api.RefreshTokenService;
import net.rose.rose_identity_service.auth.persistence.entity.AuthEntity;
import net.rose.rose_identity_service.auth.persistence.entity.EmailVerificationTokenEntity;
import net.rose.rose_identity_service.auth.persistence.entity.PasswordResetTokenEntity;
import net.rose.rose_identity_service.auth.persistence.repository.api.AuthRepository;
import net.rose.rose_identity_service.auth.persistence.repository.api.EmailVerificationTokenRepository;
import net.rose.rose_identity_service.auth.persistence.repository.api.PasswordResetTokenRepository;
import net.rose.rose_identity_service.auth.rest.dto.SessionDto;
import net.rose.rose_identity_service.auth.rest.dto.TokenPairDto;
import net.rose.rose_identity_service.auth.rest.dto.UserMeDto;
import net.rose.rose_identity_service.core.config.security.CurrentUser;
import net.rose.rose_identity_service.core.config.security.CurrentUserProvider;
import net.rose.rose_identity_service.core.exception.*;
import net.rose.rose_identity_service.core.persistence.id.UserId;
import net.rose.rose_identity_service.user.domain.command.CreateUserCommand;
import net.rose.rose_identity_service.user.domain.model.UserProfile;
import net.rose.rose_identity_service.user.domain.model.UserStatus;
import net.rose.rose_identity_service.user.domain.service.api.UserCommandService;
import net.rose.rose_identity_service.user.domain.service.api.UserQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
class DefaultAuthService implements AuthService {

    private static final Duration VERIFICATION_TOKEN_TTL = Duration.ofHours(24);


    private final AuthRepository authRepository;


    private final EmailVerificationTokenRepository verificationTokenRepository;


    private final UserCommandService userCommandService;


    private final UserQueryService userQueryService;


    private final JwtService jwtService;


    private final RefreshTokenService refreshTokenService;


    private final CurrentUserProvider currentUserProvider;


    private final PasswordEncoder passwordEncoder;


    private final VerificationTokenGenerator tokenGenerator;


    private final TokenHashService tokenHashService;


    private final ApplicationEventPublisher eventPublisher;


    private static final Duration PASSWORD_RESET_TOKEN_TTL = Duration.ofMinutes(30);


    private final PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    @Transactional
    public void register(RegisterCommand command) {
        if (authRepository.existsByEmail(command.email())) {
            throw EntityAlreadyExistsException.of("Auth credentials", "email", command.email());
        }

        UserId userId = userCommandService.createUser(new CreateUserCommand(
                command.firstName(),
                command.lastName(),
                command.email()
        ));

        AuthEntity authEntity = AuthEntity.builder()
                .userId(userId)
                .email(command.email())
                .passwordHash(passwordEncoder.encode(command.password()))
                .build();

        authRepository.save(authEntity);

        String rawToken = tokenGenerator.generate();
        String tokenHash = tokenHashService.hash(rawToken);

        EmailVerificationTokenEntity verificationToken = EmailVerificationTokenEntity.builder()
                .userId(userId)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(VERIFICATION_TOKEN_TTL))
                .build();

        verificationTokenRepository.save(verificationToken);

        eventPublisher.publishEvent(new VerificationTokenCreatedEvent(
                userId,
                command.email(),
                rawToken
        ));
    }


    @Override
    @Transactional
    public void verify(String token) {
        String tokenHash = tokenHashService.hash(token);

        EmailVerificationTokenEntity verificationToken = verificationTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::invalid);

        if (verificationToken.isUsed()) {
            throw InvalidTokenException.invalid();
        }

        if (verificationToken.isExpired()) {
            throw InvalidTokenException.expired();
        }

        verificationToken.setUsedAt(Instant.now());

        userCommandService.verifyUser(verificationToken.getUserId());
    }


    @Override
    @Transactional
    public TokenPairDto login(LoginCommand command) {
        AuthEntity authEntity = authRepository.findByEmail(command.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(command.password(), authEntity.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        UserProfile user = userQueryService.getById(authEntity.getUserId());

        if (user.status() == UserStatus.PENDING_VERIFICATION) {
            throw new AccountNotVerifiedException();
        }

        if (user.status() == UserStatus.DISABLED) {
            throw new AccountDisabledException();
        }

        authEntity.setLastLoginAt(Instant.now());

        String accessToken = jwtService.generateAccessToken(
                authEntity.getUserId(),
                authEntity.getEmail()
        );

        String refreshToken = refreshTokenService.create(authEntity.getUserId());

        return new TokenPairDto(accessToken, refreshToken);
    }


    @Override
    @Transactional
    public TokenPairDto refresh(String refreshToken) {
        UserId userId = refreshTokenService.validateAndGetUserId(refreshToken);

        UserProfile user = userQueryService.getById(userId);

        if (user.status() == UserStatus.PENDING_VERIFICATION) {
            throw new AccountNotVerifiedException();
        }

        if (user.status() == UserStatus.DISABLED) {
            throw new AccountDisabledException();
        }

        refreshTokenService.revoke(refreshToken);

        String newRefreshToken = refreshTokenService.create(userId);

        String newAccessToken = jwtService.generateAccessToken(
                user.id(),
                user.email()
        );

        return new TokenPairDto(newAccessToken, newRefreshToken);
    }


    @Override
    @Transactional(readOnly = true)
    public SessionDto session() {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        return new SessionDto(
                true,
                currentUser.userId().getId(),
                currentUser.email()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public UserMeDto me() {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        UserProfile user = userQueryService.getById(currentUser.userId());

        return new UserMeDto(
                user.id().getId(),
                user.firstName(),
                user.lastName(),
                user.email(),
                user.status()
        );
    }


    @Override
    @Transactional
    public void logout(String accessToken, String refreshToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw InvalidTokenException.missing();
        }

        jwtService.revoke(accessToken);
        CurrentUser currentUser = currentUserProvider.getCurrentUser();
        refreshTokenService.revokeAllForUser(currentUser.userId());
    }


    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordCommand command) {
        authRepository.findByEmail(command.email())
                .ifPresent(authEntity -> {
                    String rawToken = tokenGenerator.generate();
                    String tokenHash = tokenHashService.hash(rawToken);

                    PasswordResetTokenEntity tokenEntity = PasswordResetTokenEntity.builder()
                            .userId(authEntity.getUserId())
                            .tokenHash(tokenHash)
                            .expiresAt(Instant.now().plus(PASSWORD_RESET_TOKEN_TTL))
                            .build();

                    passwordResetTokenRepository.save(tokenEntity);

                    eventPublisher.publishEvent(new PasswordResetTokenCreatedEvent(
                            authEntity.getUserId(),
                            authEntity.getEmail(),
                            rawToken
                    ));
                });
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        String tokenHash = tokenHashService.hash(command.token());

        PasswordResetTokenEntity tokenEntity = passwordResetTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidTokenException::invalid);

        if (tokenEntity.isUsed()) {
            throw InvalidTokenException.invalid();
        }

        if (tokenEntity.isExpired()) {
            throw InvalidTokenException.expired();
        }

        AuthEntity authEntity = authRepository.findByUserId(tokenEntity.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Auth credentials not found."));

        authEntity.setPasswordHash(passwordEncoder.encode(command.newPassword()));

        tokenEntity.setUsedAt(Instant.now());

        refreshTokenService.revokeAllForUser(tokenEntity.getUserId());
    }


    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        CurrentUser currentUser = currentUserProvider.getCurrentUser();

        AuthEntity authEntity = authRepository.findByUserId(currentUser.userId())
                .orElseThrow(() -> new UnauthorizedException("Auth credentials not found."));

        if (!passwordEncoder.matches(command.oldPassword(), authEntity.getPasswordHash())) {
            throw new UnauthorizedException("Invalid old password.");
        }

        authEntity.setPasswordHash(passwordEncoder.encode(command.newPassword()));

        refreshTokenService.revokeAllForUser(currentUser.userId());
    }

}