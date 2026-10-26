package net.rose.rose_order_service.core.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, message);
    }

}
