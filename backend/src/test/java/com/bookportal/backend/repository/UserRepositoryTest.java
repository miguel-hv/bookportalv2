package com.bookportal.backend.repository;

import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("existsByUsername should return true when a user exists")
    void existsByUsername_shouldReturnTrue() {
        RoleEntity role = new RoleEntity();
        role.setName(ERole.ROLE_USER);
        entityManager.persist(role);

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("pass");
        user.setRoles(Set.of(role));

        entityManager.persist(user);
        entityManager.flush();

        boolean exists = userRepository.existsByUsername("john");
        assertTrue(exists);
    }

    @Test
    @DisplayName("findByUsername should return user when it exists")
    void findByUsername_shouldReturnUser() {
        RoleEntity role = new RoleEntity();
        role.setName(ERole.ROLE_USER);
        entityManager.persist(role);

        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setPassword("pass");
        user.setRoles(Set.of(role));

        entityManager.persist(user);
        entityManager.flush();

        var found = userRepository.findByUsername("john");

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
    }

    @Test
    @DisplayName("findByUsername should return empty when user does not exist")
    void findByUsername_shouldReturnEmpty() {
        var found = userRepository.findByUsername("ghost");

        assertTrue(found.isEmpty());
    }
}
