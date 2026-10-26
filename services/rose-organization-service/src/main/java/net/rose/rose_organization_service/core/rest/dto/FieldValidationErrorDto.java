package net.rose.rose_organization_service.core.rest.dto;

public record FieldValidationErrorDto(
        String field,
        String message,
        Object rejectedValue
) {
}
