package com.bookportal.backend.security;

import com.bookportal.backend.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserRepository userRepository;

    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isOwner(Long userId, String username) {
        return userRepository.findById(userId)
                .map(user -> user.getUsername().equals(username))
                .orElse(false);
    }
}
