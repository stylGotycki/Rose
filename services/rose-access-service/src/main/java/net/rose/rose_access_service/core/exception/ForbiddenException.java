package net.rose.rose_access_service.core.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, message);
    }

}
