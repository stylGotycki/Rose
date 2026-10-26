package net.rose.rose_billing_service.core.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST, message);
    }

}
