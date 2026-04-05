package com.bookportal.backend.controller;

import com.bookportal.backend.domain.model.Role;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.domain.model.enums.ERole;
import com.bookportal.backend.infrastructure.repository.RoleRepository;
import com.bookportal.backend.infrastructure.repository.UserRepository;
import com.bookportal.backend.service.JwtService;
import com.bookportal.backend.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role roleUser = new Role();
        roleUser.setName(ERole.ROLE_USER);
        Role roleAdmin = new Role();
        roleAdmin.setName(ERole.ROLE_ADMIN);

        roleRepository.save(roleUser);
        roleRepository.save(roleAdmin);

        // Admin user
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("password"));
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleAdmin);
        adminRoles.add(roleUser);
        adminUser.setRoles(adminRoles);
        userRepository.save(adminUser);

        // Regular user
        regularUser = new User();
        regularUser.setUsername("john");
        regularUser.setPassword(passwordEncoder.encode("password"));
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(roleUser);
        regularUser.setRoles(userRoles);
        userRepository.save(regularUser);
    }

    @Test
    void getUserList_shouldReturnAllUsers_whenAuthenticated() throws Exception {
        String token = jwtService.generateToken(adminUser);

        mockMvc.perform(get("/api/user/user-list")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[1].username").exists());
    }

    @Test
    void deleteUser_shouldAllowAdmin() throws Exception {
        String token = jwtService.generateToken(adminUser);

        mockMvc.perform(delete("/api/user/" + regularUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    void deleteUser_shouldRejectRegularUser() throws Exception {
        String token = jwtService.generateToken(regularUser);

        mockMvc.perform(delete("/api/user/" + adminUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
