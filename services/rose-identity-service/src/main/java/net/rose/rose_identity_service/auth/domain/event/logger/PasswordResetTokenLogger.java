package net.rose.rose_identity_service.auth.domain.event.logger;

import lombok.extern.slf4j.Slf4j;
import net.rose.rose_identity_service.auth.domain.event.PasswordResetTokenCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class PasswordResetTokenLogger {

    @EventListener
    void on(PasswordResetTokenCreatedEvent event) {
        log.info(
                "Password reset link for {}: http://localhost:8080/api/auth/password/reset?token={}",
                event.email(),
                event.rawToken()
        );
    }
}