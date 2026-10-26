package net.rose.rose_order_service.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {

    private final ErrorCode errorCode;


    private final HttpStatus status;


    protected AppException(ErrorCode errorCode, HttpStatus status, String message) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }


    protected AppException(ErrorCode errorCode, HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.status = status;
    }

}
