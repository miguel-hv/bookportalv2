package com.bookportal.backend.controller;

import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/user-list")
    public List<UserEntity> getUserList() {
        return service.getAllUsers();
    }
}
