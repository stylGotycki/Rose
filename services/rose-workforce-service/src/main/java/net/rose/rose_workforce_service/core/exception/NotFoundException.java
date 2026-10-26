package net.rose.rose_workforce_service.core.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }


    public static NotFoundException of(String resourceName) {
        return new NotFoundException(resourceName + " not found.");
    }


    public static NotFoundException of(String resourceName, Object id) {
        return new NotFoundException(resourceName + " with id " + id + " not found.");
    }

}
