package com.bookportal.backend.controller;


import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.entity.RefreshTokenEntity;
import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.exception.AuthException;
import com.bookportal.backend.exception.ValidationException;
import com.bookportal.backend.model.LoginRequest;
import com.bookportal.backend.model.RegisterRequest;
import com.bookportal.backend.repository.RefreshTokenRepository;
import com.bookportal.backend.repository.RoleRepository;
import com.bookportal.backend.service.RefreshTokenService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.service.JwtService;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          RefreshTokenService refreshTokenService,
                          RefreshTokenRepository refreshTokenRepository,
                          RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.roleRepository = roleRepository;
    }

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;
    @Value("${cookie.sameSite:Lax}")
    private String cookieSameSite;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException(ErrorMessages.USERNAME_EXISTS.getMessage());
        }

        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new ValidationException(ErrorMessages.NO_ROLES_FOUND.getMessage());
        }

        String normalizedRole = request.getRole().toUpperCase();
            normalizedRole = "ROLE_" + normalizedRole;

        ERole eRole;
        try {
            eRole = ERole.valueOf(normalizedRole);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Invalid role: " + request.getRole());
        }

        Set<RoleEntity> roles = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.ROLE_NOT_FOUND.getMessage()));
        roles.add(userRole);

        if (eRole == ERole.ROLE_ADMIN) {
            RoleEntity adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException(ErrorMessages.ROLE_NOT_FOUND.getMessage()));
            roles.add(adminRole);
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse(ErrorMessages.USER_REGISTERED.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,  HttpServletResponse response) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new AuthException(ErrorMessages.INVALID_CREDENTIALS.getMessage());
        }

        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new AuthException(ErrorMessages.MISSING_REFRESH_TOKEN.getMessage());
        }

        return refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user);
                    ResponseCookie newCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                            .httpOnly(true)
                            .secure(cookieSecure)
                            .sameSite(cookieSameSite)
                            .path("/api")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

                    response.addHeader(HttpHeaders.SET_COOKIE, newCookie.toString());

                    return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
                })
                .orElseThrow(() -> new AuthException(ErrorMessages.INVALID_REFRESH_TOKEN.getMessage()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletResponse response) {

        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
        }

        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/api")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResponseEntity.ok(Map.of("message", SuccessMessages.LOGGED_OUT.getMessage()));
    }


}
