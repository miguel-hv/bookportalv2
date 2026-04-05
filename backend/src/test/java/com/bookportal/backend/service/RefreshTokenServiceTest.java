package com.bookportal.backend.service;

import com.bookportal.backend.domain.model.RefreshToken;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.infrastructure.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();

        // manually inject refreshTokenDurationMs since @Value won't work in a plain unit test
        refreshTokenService = new RefreshTokenService(refreshTokenRepository);
        setRefreshTokenDurationMs(60000L); // 1 minute
    }

    // Helper method to set private field @Value manually
    private void setRefreshTokenDurationMs(Long value) {
        try {
            java.lang.reflect.Field field = RefreshTokenService.class.getDeclaredField("refreshTokenDurationMs");
            field.setAccessible(true);
            field.set(refreshTokenService, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createRefreshToken_shouldGenerateAndSaveToken() {
        // Arrange
        RefreshToken savedToken = new RefreshToken();
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(user);

        // Assert
        assertNotNull(result);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

        // Capture and inspect saved token
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken captured = captor.getValue();

        assertEquals(user, captured.getUser());
        assertNotNull(captured.getToken());
        assertTrue(captured.getExpiryDate().isAfter(Instant.now()));
    }

    @Test
    void verifyExpiration_shouldReturnTokenIfNotExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_shouldThrowAndDeleteIfExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(10));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));

        verify(refreshTokenRepository, times(1)).delete(token);
    }

    @Test
    void deleteByUser_shouldCallRepository() {
        refreshTokenService.deleteByUser(user);

        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}
