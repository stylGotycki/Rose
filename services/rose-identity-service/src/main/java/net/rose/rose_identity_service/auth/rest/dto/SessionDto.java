package net.rose.rose_identity_service.auth.rest.dto;

import java.util.UUID;

public record SessionDto(
    boolean authenticated,
    UUID userId,
    String email
) {
}
