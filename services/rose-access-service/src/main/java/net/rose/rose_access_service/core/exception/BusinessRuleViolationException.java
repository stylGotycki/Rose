package net.rose.rose_access_service.core.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleViolationException extends AppException {

    public BusinessRuleViolationException(String message) {
        super(ErrorCode.BUSINESS_RULE_VIOLATION, HttpStatus.CONFLICT, message);
    }

}
