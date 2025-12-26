package com.bookportal.backend.config;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.exception.AuthException;
import com.bookportal.backend.util.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorMessages error = ErrorMessages.UNAUTHORIZED;

        // If the cause is your domain exception, extract it
        if (authException.getCause() instanceof AuthException authEx) {
            error = authEx.getError();
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        MessageResponse body = new MessageResponse(error.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
