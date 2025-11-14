package com.bookportal.backend.controller;

import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.entity.RefreshTokenEntity;
import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.repository.RefreshTokenRepository;
import com.bookportal.backend.repository.RoleRepository;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Dynamically provide a base64-encoded HS256 secret for JwtService
    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
        registry.add("jwt.secret", () -> base64);
        registry.add("jwt.expiration", () -> 60000L); // 1 minute tokens for tests
        registry.add("jwt.refreshExpiration", () -> 604800000L); // 7 days
        registry.add("frontendUrl", () -> "http://localhost");
        // cookie properties (optional) to match defaults used by controller
        registry.add("cookie.secure", () -> false);
        registry.add("cookie.sameSite", () -> "Lax");
    }

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Ensure roles exist in DB for register/login flows
        RoleEntity userRole = new RoleEntity();
        userRole.setName(ERole.ROLE_USER);
        roleRepository.save(userRole);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(ERole.ROLE_ADMIN);
        roleRepository.save(adminRole);
    }

    @Test
    void register_shouldCreateUserAndReturnMessage() throws Exception {
        Map<String, String> body = Map.of(
                "username", "int-john",
                "password", "password",
                "role", "USER"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // Body is a MessageResponse record: {"message":"..."}
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("message").asText()).isEqualTo(ErrorMessages.USER_REGISTERED.getMessage());

        // Verify user persisted
        assertThat(userRepository.existsByUsername("int-john")).isTrue();
    }

    @Test
    void login_shouldReturnAccessTokenAndSetRefreshCookie() throws Exception {
        // create user in DB (roles required)
        RoleEntity role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
        UserEntity user = new UserEntity();
        user.setUsername("login-john");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        Map<String, String> login = Map.of(
                "username", "login-john",
                "password", "password"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", containsString("refreshToken=")))
                .andReturn();

        // extract cookie value
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("refreshToken=");
        // confirm refresh token exists in DB
        // parse token value between "refreshToken=" and ';'
        String refreshTokenValue = extractCookieValue(setCookie, "refreshToken");
        assertThat(refreshTokenRepository.findByToken(refreshTokenValue)).isPresent();
    }

    @Test
    void refresh_shouldReturnNewAccessTokenAndCookie() throws Exception {
        // create user and a refresh token in DB
        RoleEntity role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
        UserEntity user = new UserEntity();
        user.setUsername("refresh-john");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        RefreshTokenEntity rt = new RefreshTokenEntity();
        rt.setUser(user);
        String tokenValue = UUID.randomUUID().toString();
        rt.setToken(tokenValue);
        rt.setExpiryDate(Instant.now().plusSeconds(60));
        refreshTokenRepository.save(rt);

        // call refresh endpoint with Cookie header
        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", tokenValue))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(header().string("Set-Cookie", containsString("refreshToken=")))
                .andReturn();

        String newSetCookie = result.getResponse().getHeader("Set-Cookie");
        String newRefreshTokenValue = extractCookieValue(newSetCookie, "refreshToken");
        assertThat(newRefreshTokenValue).isNotEmpty();
        assertThat(refreshTokenRepository.findByToken(newRefreshTokenValue)).isPresent();
    }

    @Test
    void logout_shouldClearCookieAndDeleteRefreshToken() throws Exception {
        // create user and a refresh token in DB
        RoleEntity role = roleRepository.findByName(ERole.ROLE_USER).orElseThrow();
        UserEntity user = new UserEntity();
        user.setUsername("logout-john");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(Set.of(role));
        userRepository.save(user);

        RefreshTokenEntity rt = new RefreshTokenEntity();
        rt.setUser(user);
        String tokenValue = UUID.randomUUID().toString();
        rt.setToken(tokenValue);
        rt.setExpiryDate(Instant.now().plusSeconds(60));
        refreshTokenRepository.save(rt);

        // logout with cookie
        MvcResult result = mockMvc.perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("refreshToken", tokenValue)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", containsString("refreshToken=")))
                .andReturn();

        // cookie was cleared (max-age=0 or empty value)
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("refreshToken=");
        // token should be deleted from repository
        assertThat(refreshTokenRepository.findByToken(tokenValue)).isEmpty();
    }

    // --- helper ---

    private static String extractCookieValue(String setCookieHeader, String cookieName) {
        if (setCookieHeader == null) return null;
        String prefix = cookieName + "=";
        int start = setCookieHeader.indexOf(prefix);
        if (start == -1) return null;
        start += prefix.length();
        int end = setCookieHeader.indexOf(';', start);
        if (end == -1) end = setCookieHeader.length();
        return setCookieHeader.substring(start, end);
    }
}
