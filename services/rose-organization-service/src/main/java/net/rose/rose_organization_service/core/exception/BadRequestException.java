package net.rose.rose_organization_service.core.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, message);
    }

}
