package com.bookportal.backend.util;

public enum ErrorMessages {
    USERNAME_EXISTS("Username already exists"),
    USER_REGISTERED("User registered successfully"),
    INVALID_CREDENTIALS("Invalid username or password"),
    INVALID_REFRESH_TOKEN("Invalid or expired refresh token"),
    MISSING_REFRESH_TOKEN("Missing refresh token"),
    UNAUTHORIZED("Unauthorized"),
    JWT_EXPIRED("JWT expired"),
    INVALID_JWT("Invalid JWT"),
    INVALID_ROLE("Invalid role."),
    NO_ROLES_FOUND("User must have at least one role"),
    UNEXPECTED_ERROR("An unexpected error occurred.");

    private final String message;

    ErrorMessages(String message) { this.message = message; }

    public String getMessage() { return message; }
}
