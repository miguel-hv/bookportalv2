package com.bookportal.backend.service;

import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService (UserRepository repository){
        this.repository = repository;
    }

    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    public void deleteUserById(Long id) {
        repository.deleteById(id);
    }

}
