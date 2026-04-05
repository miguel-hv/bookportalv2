package com.bookportal.backend.infrastructure.repository;

import com.bookportal.backend.domain.model.RefreshTokenEntity;
import com.bookportal.backend.domain.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUser(UserEntity user);
}

