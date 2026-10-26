package net.rose.rose_identity_service.auth.domain.service.impl;

import lombok.RequiredArgsConstructor;
import net.rose.rose_identity_service.auth.domain.service.api.EmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class SpringEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${rose.mail.from:no-reply@rose.local}")
    private String from;

    @Override
    public void send(
            String to,
            String subject,
            String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}