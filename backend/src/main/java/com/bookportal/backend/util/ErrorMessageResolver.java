package com.bookportal.backend.util;

import java.util.Map;

public class ErrorMessageResolver {

    private static final Map<String, String> MESSAGE_MAPPING = Map.of(
            "expired", ErrorMessages.JWT_EXPIRED.getMessage(),
            "invalid", ErrorMessages.INVALID_JWT.getMessage(),
            "malformed", ErrorMessages.INVALID_JWT.getMessage(),
            "signature", ErrorMessages.INVALID_JWT.getMessage()
    );

    public static String resolve(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return ErrorMessages.UNAUTHORIZED.getMessage();
        }

        String lower = rawMessage.toLowerCase();
        return MESSAGE_MAPPING.entrySet().stream()
                .filter(e -> lower.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(ErrorMessages.UNAUTHORIZED.getMessage());
    }
}
