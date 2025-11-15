package com.bookportal.backend.service;

import com.bookportal.backend.dto.RoleDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService (UserRepository repository){
        this.repository = repository;
    }

    public List<UserDto> getAllUsers() {
        return repository.findAll()
                .stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles().stream()
                                .map(r -> r.getName().name())
                                .collect(Collectors.toSet())
                ))
                .toList();
    }

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

}
