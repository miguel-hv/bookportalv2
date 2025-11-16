package com.bookportal.backend.controller;

import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.service.UserService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private UserDto user1;
    private UserDto user2;
    private List<UserDto> users;

    @BeforeEach
    void setUp() {
        user1 = new UserDto();
        user1.setUsername("john");

        user2 = new UserDto();
        user2.setUsername("jane");

        users = List.of(user1, user2);
    }

    @Test
    void getUserList_shouldReturnListOfUsers() {
        when(userService.getAllUsers()).thenReturn(users);

        List<UserDto> result = controller.getUserList();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("john", result.get(0).getUsername());
        assertEquals("jane", result.get(1).getUsername());

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser_shouldReturnForbidden_whenUserIsNotAdmin() {
        // Inline Authentication implementation for a non-admin user
        Authentication auth = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_USER")); // not admin
            }
            @Override public Object getCredentials() { return null; }
            @Override public Object getDetails() { return null; }
            @Override public Object getPrincipal() { return null; }
            @Override public boolean isAuthenticated() { return true; }
            @Override public void setAuthenticated(boolean isAuthenticated) {}
            @Override public String getName() { return "user"; }
        };

        // Call the controller method
        ResponseEntity<?> response = controller.deleteUser(1L, auth);

        // Assertions
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(ErrorMessages.NOT_ALLOWED_ROLE.getMessage(), response.getBody());

        // Verify service method was never called
        verify(userService, never()).deleteUserById(anyLong());
    }

    @Test
    void deleteUser_shouldReturnSuccess_whenUserIsAdmin() {
        Authentication auth = new Authentication() {
            @Override public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            @Override public Object getCredentials() { return null; }
            @Override public Object getDetails() { return null; }
            @Override public Object getPrincipal() { return null; }
            @Override public boolean isAuthenticated() { return true; }
            @Override public void setAuthenticated(boolean isAuthenticated) {}
            @Override public String getName() { return "admin"; }
        };

        doNothing().when(userService).deleteUserById(1L);

        ResponseEntity<?> response = controller.deleteUser(1L, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(SuccessMessages.USER_DELETED.getMessage(), response.getBody());
        verify(userService).deleteUserById(1L);
    }
}
