package net.rose.rose_identity_service.auth.domain.service.impl;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class VerificationTokenGenerator {

    private static final int TOKEN_BYTES = 32;


    private final SecureRandom secureRandom = new SecureRandom();


    String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

}