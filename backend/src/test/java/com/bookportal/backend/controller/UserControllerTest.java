package com.bookportal.backend.controller;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.dto.UserBookDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.domain.model.UserEntity;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

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
    void getUserById_shouldReturnUser() {
        when(userService.getUserById(1L)).thenReturn(user1);

        UserDto result = controller.getUserById(1L);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        verify(userService).getUserById(1L);
    }

    @Test
    void deleteUser_shouldCallService() {
        doNothing().when(userService).deleteUserById(1L);

        ResponseEntity<?> response = controller.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        assertEquals(SuccessMessages.USER_DELETED.getMessage(), ((MessageResponse) response.getBody()).message());
        verify(userService).deleteUserById(1L);
    }
}
