package net.rose.rose_identity_service.core.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends AppException {

    public EntityAlreadyExistsException(String message) {
        super(ErrorCode.ENTITY_ALREADY_EXISTS, HttpStatus.CONFLICT, message);
    }


    public static EntityAlreadyExistsException of(String resourceName) {
        return new EntityAlreadyExistsException(resourceName + " already exists.");
    }


    public static EntityAlreadyExistsException of(String resourceName, String fieldName, Object value) {
        return new EntityAlreadyExistsException(
                resourceName + " with " + fieldName + " '" + value + "' already exists."
        );
    }

}
