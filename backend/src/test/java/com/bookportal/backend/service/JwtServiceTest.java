package com.bookportal.backend.service;

import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    // Example secret key (MUST be at least 256 bits for HS256)
    private final String secret = Base64.getEncoder().encodeToString(
            Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded()
    );

    @BeforeEach
    void setUp() {
        // 1 minute expiration for tests
        jwtService = new JwtService(secret, 60000L);
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(token.length() > 10, "Token should not be empty");
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("testuser", extractedUsername);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        // Expiration = 1 ms for testing expiration logic
        JwtService shortLivedJwtService = new JwtService(secret, 1L);
        String token = shortLivedJwtService.generateToken(userDetails);

        Thread.sleep(10); // wait until it expires

        boolean isValid;
        try {
            isValid = shortLivedJwtService.isTokenValid(token, userDetails);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            isValid = false; // expected behavior
        }

        assertFalse(isValid);
    }
}
