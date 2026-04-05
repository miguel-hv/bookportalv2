package com.bookportal.backend.repository;

import com.bookportal.backend.domain.model.Role;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.domain.model.enums.ERole;
import com.bookportal.backend.infrastructure.repository.UserRepository;
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
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        entityManager.persist(role);

        User user = new User();
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
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        entityManager.persist(role);

        User user = new User();
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
