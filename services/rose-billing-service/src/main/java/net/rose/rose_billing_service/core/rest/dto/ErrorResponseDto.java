package net.rose.rose_billing_service.core.rest.dto;

import lombok.Builder;
import net.rose.rose_billing_service.core.exception.ErrorCode;

import java.time.Instant;
import java.util.List;

@Builder
public record ErrorResponseDto(
        Instant timestamp,
        int status,
        ErrorCode errorCode,
        String message,
        String path,
        List<FieldValidationErrorDto> errors
) {

    // todo: remove this v
    public static ErrorResponseDto of(
            int status,
            ErrorCode errorCode,
            String message,
            String path
    ) {
        return new ErrorResponseDto(
                Instant.now(),
                status,
                errorCode,
                message,
                path,
                List.of()
        );
    }

    public static ErrorResponseDto of(
            int status,
            ErrorCode errorCode,
            String message,
            String path,
            List<FieldValidationErrorDto> errors
    ) {
        return new ErrorResponseDto(
                Instant.now(),
                status,
                errorCode,
                message,
                path,
                errors
        );
    }


}
