package com.bookportal.backend.repository;

import com.bookportal.backend.entity.RefreshTokenEntity;
import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserEntity createUserWithRole() {
        RoleEntity role = new RoleEntity();
        role.setName(ERole.ROLE_USER);
        entityManager.persist(role);

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRoles(Set.of(role));  // REQUIRED by your UserEntity validation

        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    @Test
    void findByToken_shouldReturnRefreshToken() {
        UserEntity user = createUserWithRole();

        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(60));

        entityManager.persist(token);
        entityManager.flush();

        var found = refreshTokenRepository.findByToken(token.getToken());

        assertTrue(found.isPresent());
        assertEquals(token.getToken(), found.get().getToken());
    }

    @Test
    void deleteByUser_shouldRemoveTokensForThatUser() {
        UserEntity user = createUserWithRole();

        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(60));

        entityManager.persist(token);
        entityManager.flush();

        refreshTokenRepository.deleteByUser(user);
        entityManager.flush();

        assertTrue(refreshTokenRepository.findAll().isEmpty());
    }
}
