package net.rose.rose_identity_service.auth.domain.event.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rose.rose_identity_service.auth.domain.event.VerificationTokenCreatedEvent;
import net.rose.rose_identity_service.auth.domain.service.api.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
class VerificationTokenEmailSender {

    private final EmailSender emailSender;

    @Value("${rose.mail.verification-base-url:http://localhost:8080}")
    private String verificationBaseUrl;

    @EventListener
    void on(VerificationTokenCreatedEvent event) {
        String verificationUrl = UriComponentsBuilder
                .fromUriString(verificationBaseUrl)
                .path("/verify")
                .queryParam("token", event.rawToken())
                .build()
                .toUriString();

        String body = """
                Hello,

                Your ROSE account has been created.

                Click the link below to activate your account:

                %s

                If you did not create this account, ignore this message.
                """.formatted(verificationUrl);

        emailSender.send(
                event.email(),
                "Activate your ROSE account",
                body
        );

        log.info("Verification email sent to {}", event.email());
    }
}