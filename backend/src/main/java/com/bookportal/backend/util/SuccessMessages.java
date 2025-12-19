package com.bookportal.backend.util;

public enum SuccessMessages {
    USER_REGISTERED("User registered successfully"),
    TOKEN_REFRESHED("Access token refreshed successfully"),
    USER_DELETED("User deleted successfully"),
    LOGGED_OUT("Logged out successfully"),
    BOOK_DELETED("Book deleted successfully");

    private final String message;

    SuccessMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
