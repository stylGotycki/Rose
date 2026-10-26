package net.rose.rose_identity_service.core.rest.dto;

public record FieldValidationErrorDto(
        String field,
        String message,
        Object rejectedValue
) {
}
