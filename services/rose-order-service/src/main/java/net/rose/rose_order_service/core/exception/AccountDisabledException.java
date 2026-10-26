package net.rose.rose_order_service.core.exception;

import org.springframework.http.HttpStatus;

public class AccountDisabledException extends AppException {

    public AccountDisabledException() {
        super(
                ErrorCode.ACCOUNT_DISABLED,
                HttpStatus.FORBIDDEN,
                "Account is disabled."
        );
    }
}
