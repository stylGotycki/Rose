package net.rose.rose_billing_service.core.config.security.client;

import java.util.UUID;

public record IdentityCurrentUserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String status
) {
}