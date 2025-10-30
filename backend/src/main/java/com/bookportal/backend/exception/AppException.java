package com.bookportal.backend.exception;

public abstract class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}
