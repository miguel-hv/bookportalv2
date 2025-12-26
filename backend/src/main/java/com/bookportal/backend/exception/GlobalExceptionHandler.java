package com.bookportal.backend.exception;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.util.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Authentication or authorization errors → 401
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<MessageResponse> handleAuth(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(ex.getError().getMessage()));
    }

    // 🔹 Validation errors → 400
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<MessageResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(ex.getMessage()));
    }

    // 🔹 Resource not found → 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(ex.getMessage()));
    }

    // 🔹 Application-level catch-all (for your AppException hierarchy)
    @ExceptionHandler(AppException.class)
    public ResponseEntity<MessageResponse> handleAppExceptions(AppException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(ex.getMessage()));
    }

    // 🔹 Fallback for truly unexpected errors → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse(ErrorMessages.UNEXPECTED_ERROR.getMessage()));
    }
}
