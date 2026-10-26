package net.rose.rose_access_service.core.exception;

import org.springframework.http.HttpStatus;

public class AccountNotVerifiedException extends AppException {

    public AccountNotVerifiedException() {
        super(
                ErrorCode.ACCOUNT_NOT_VERIFIED,
                HttpStatus.FORBIDDEN,
                "Account is not verified."
        );
    }
}
