package com.bookportal.backend.exception;

import com.bookportal.backend.util.ErrorMessages;

public class AuthException extends AppException {

    private final ErrorMessages error;

    public AuthException(ErrorMessages error) {
        super(error.getMessage());
        this.error = error;
    }

    public ErrorMessages getError() {
        return error;
    }
}
