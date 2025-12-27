package com.bookportal.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Schema(
            example = "john_doe",
            description = "Unique username"
    )
    private String username;

    @NotBlank
    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one number, one uppercase letter, and one special character"
    )
    @Schema(
            description = """
            Password requirements:
            • Minimum length: 5
            • At least one uppercase letter
            • At least one number
            • At least one special character (@#$%^&+=!)
            """,
            example = "Pass1!"
    )
    private String password;
    private String role;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
