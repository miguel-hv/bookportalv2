package com.bookportal.backend.service;

import com.bookportal.backend.dto.UserBookDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUsername("alice");

        user2 = new User();
        user2.setUsername("bob");
    }

    @Test
    void getAllUsers_ShouldReturnUsers_WhenRepositoryHasData() {

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result)
                .hasSize(2)
                .extracting(UserDto::getUsername)
                .containsExactlyInAnyOrder("alice", "bob");

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenRepositoryIsEmpty() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserById_ShouldCallRepositoryDeleteById() {

        Long userId = 42L;

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}
