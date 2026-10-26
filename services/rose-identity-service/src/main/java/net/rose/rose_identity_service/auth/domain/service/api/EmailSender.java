package net.rose.rose_identity_service.auth.domain.service.api;

public interface EmailSender {

    void send(
            String to,
            String subject,
            String body
    );
}