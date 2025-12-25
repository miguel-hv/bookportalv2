package com.bookportal.backend.service;

import com.bookportal.backend.dto.UserBookDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.exception.ResourceNotFoundException;
import com.bookportal.backend.mapper.UserMapper;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.util.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService (UserRepository repository){
        this.repository = repository;
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDto getUserById(Long userId) {
         return repository.findById(userId).map(UserMapper::toDto)
                 .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage()));
    }

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

}
