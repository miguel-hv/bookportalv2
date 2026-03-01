package com.bookportal.backend.controller;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.dto.UserBookDto;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.service.UserService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/user-list")
    public List<UserDto> getUserList() {
        return service.getAllUsers();
    }

    @GetMapping("/user/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        service.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse(SuccessMessages.USER_DELETED.getMessage()));
    }

}
