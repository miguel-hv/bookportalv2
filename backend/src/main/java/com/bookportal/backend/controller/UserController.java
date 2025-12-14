package com.bookportal.backend.controller;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.dto.UserDto;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.service.UserService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(ErrorMessages.NOT_ALLOWED_ROLE.getMessage()));
        }

        service.deleteUserById(id);
        return ResponseEntity.ok(new MessageResponse(SuccessMessages.USER_DELETED.getMessage()));
    }

}
