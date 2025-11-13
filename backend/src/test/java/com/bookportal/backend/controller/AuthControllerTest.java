package com.bookportal.backend.controller;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.exception.ValidationException;
import com.bookportal.backend.model.RegisterRequest;
import com.bookportal.backend.repository.RoleRepository;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.service.JwtService;
import com.bookportal.backend.service.RefreshTokenService;
import com.bookportal.backend.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private com.bookportal.backend.repository.RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthController controller;

    private RegisterRequest registerRequest;

    private RoleEntity userRole;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("john");
        registerRequest.setPassword("password");
        registerRequest.setRole("USER");

        userRole = new RoleEntity();
        userRole.setName(ERole.ROLE_USER);
    }

    @Test
    void register_shouldSaveUser_whenValidRequest() {
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<?> response = controller.register(registerRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse body = (MessageResponse) response.getBody();
        assertEquals(ErrorMessages.USER_REGISTERED.getMessage(), body.message());

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void register_shouldThrowValidationException_whenUsernameExists() {
        when(userRepository.existsByUsername("john")).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.register(registerRequest);
        });

        assertEquals(ErrorMessages.USERNAME_EXISTS.getMessage(), exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowValidationException_whenRoleIsBlank() {
        registerRequest.setRole("");
        when(userRepository.existsByUsername("john")).thenReturn(false);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.register(registerRequest);
        });

        assertEquals(ErrorMessages.NO_ROLES_FOUND.getMessage(), exception.getMessage());
        verify(userRepository, never()).save(any());
    }

}
