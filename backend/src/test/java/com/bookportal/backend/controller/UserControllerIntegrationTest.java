package com.bookportal.backend.controller;

import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.repository.RoleRepository;
import com.bookportal.backend.repository.UserRepository;
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

    private UserEntity adminUser;
    private UserEntity regularUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        RoleEntity roleUser = new RoleEntity();
        roleUser.setName(ERole.ROLE_USER);
        RoleEntity roleAdmin = new RoleEntity();
        roleAdmin.setName(ERole.ROLE_ADMIN);

        roleRepository.save(roleUser);
        roleRepository.save(roleAdmin);

        // Admin user
        adminUser = new UserEntity();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("password"));
        Set<RoleEntity> adminRoles = new HashSet<>();
        adminRoles.add(roleAdmin);
        adminRoles.add(roleUser);
        adminUser.setRoles(adminRoles);
        userRepository.save(adminUser);

        // Regular user
        regularUser = new UserEntity();
        regularUser.setUsername("john");
        regularUser.setPassword(passwordEncoder.encode("password"));
        Set<RoleEntity> userRoles = new HashSet<>();
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
                .andExpect(content().string("User deleted successfully")); // adjust to SuccessMessages.USER_DELETED.getMessage()
    }

    @Test
    void deleteUser_shouldRejectRegularUser() throws Exception {
        String token = jwtService.generateToken(regularUser);

        mockMvc.perform(delete("/api/user/" + adminUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ErrorMessages.NOT_ALLOWED_ROLE.getMessage()));
    }
}
