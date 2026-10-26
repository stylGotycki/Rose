package net.rose.rose_order_service.core.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AppException {

    private InvalidTokenException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.UNAUTHORIZED, message);
    }


    public static InvalidTokenException expired() {
        return new InvalidTokenException(ErrorCode.EXPIRED_TOKEN, "Token is expired.");
    }


    public static InvalidTokenException invalid() {
        return new InvalidTokenException(ErrorCode.INVALID_TOKEN, "Token is invalid.");
    }


    public static InvalidTokenException missing() {
        return new InvalidTokenException(ErrorCode.MISSING_TOKEN, "Token is missing.");
    }
}
