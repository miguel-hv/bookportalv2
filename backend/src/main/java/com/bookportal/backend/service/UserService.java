package com.bookportal.backend.service;

import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.mapper.UserMapper;
import com.bookportal.backend.repository.UserRepository;
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

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

}
