package net.rose.rose_workforce_service.core.rest.dto;

public record FieldValidationErrorDto(
        String field,
        String message,
        Object rejectedValue
) {
}
