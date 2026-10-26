package net.rose.rose_identity_service.auth.domain.event.logger;

import lombok.extern.slf4j.Slf4j;
import net.rose.rose_identity_service.auth.domain.event.VerificationTokenCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class VerificationTokenLogger {

    @EventListener
    void on(VerificationTokenCreatedEvent event) {
        log.info(
                "Verification link for {}: http://localhost:8080/api/auth/verify?token={}",
                event.email(),
                event.rawToken()
        );
    }
}