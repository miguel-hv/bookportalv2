package com.bookportal.backend.util;

public enum ErrorMessages {
    USERNAME_EXISTS("Username already exists"),
    USER_REGISTERED("User registered successfully"),
    USER_NOT_FOUND("User not found"),
    INVALID_CREDENTIALS("Invalid username or password"),
    INVALID_REFRESH_TOKEN("Invalid or expired refresh token"),
    MISSING_REFRESH_TOKEN("Missing refresh token"),
    UNAUTHORIZED("Unauthorized"),
    JWT_EXPIRED("JWT expired"),
    REFRESH_TOKEN_EXPIRED("Refresh token expired"),
    INVALID_JWT("Invalid JWT"),
    INVALID_ROLE("Invalid role."),
    NO_ROLES_FOUND("User must have at least one role"),
    ROLE_NOT_FOUND("Role not found"),
    NOT_ALLOWED_ROLE("Role not allowed"),
    NOT_ALLOWED_USER_ID("User id not allowed"),
    UNEXPECTED_ERROR("An unexpected error occurred."),
    BOOK_NOT_FOUND("Book not found");

    private final String message;

    ErrorMessages(String message) { this.message = message; }

    public String getMessage() { return message; }
}
