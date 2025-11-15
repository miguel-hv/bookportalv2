package com.bookportal.backend.dto;

import com.bookportal.backend.entity.RoleEntity;

import java.util.Set;

public class UserDto {
    private Long id;
    private String username;
    private  Set<String> role;

    public UserDto() {}

    public UserDto(Long id, String username,  Set<String> role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public   Set<String> getRole() { return role; }
    public void setRole( Set<String> role) { this.role = role; }
}
