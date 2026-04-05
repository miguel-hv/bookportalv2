package com.bookportal.backend.service;

import com.bookportal.backend.domain.model.RefreshToken;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.infrastructure.repository.RefreshTokenRepository;
import com.bookportal.backend.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(refreshTokenDurationMs)
        );
        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(ErrorMessages.REFRESH_TOKEN_EXPIRED.getMessage());
        }
        return token;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}