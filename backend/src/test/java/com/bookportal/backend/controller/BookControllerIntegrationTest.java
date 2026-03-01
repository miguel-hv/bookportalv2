package com.bookportal.backend.controller;

import com.bookportal.backend.entity.BookEntity;
import com.bookportal.backend.entity.RoleEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.entity.enums.ERole;
import com.bookportal.backend.repository.BookRepository;
import com.bookportal.backend.repository.RefreshTokenRepository;
import com.bookportal.backend.repository.RoleRepository;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserEntity ownerUser;
    private UserEntity otherUser;
    private String ownerToken;
    private String otherUserToken;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64 = Base64.getEncoder().encodeToString(key.getEncoded());
        registry.add("jwt.secret", () -> base64);
        registry.add("jwt.expiration", () -> 60000L);
        registry.add("jwt.refreshExpiration", () -> 604800000L);
        registry.add("frontendUrl", () -> "http://localhost");
        registry.add("cookie.secure", () -> false);
        registry.add("cookie.sameSite", () -> "Lax");
    }

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        RoleEntity userRole = new RoleEntity();
        userRole.setName(ERole.ROLE_USER);
        roleRepository.save(userRole);

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(ERole.ROLE_ADMIN);
        roleRepository.save(adminRole);

        ownerUser = new UserEntity();
        ownerUser.setUsername("owner");
        ownerUser.setPassword(passwordEncoder.encode("Pass1!"));
        Set<RoleEntity> ownerRoles = new HashSet<>();
        ownerRoles.add(userRole);
        ownerUser.setRoles(ownerRoles);
        userRepository.save(ownerUser);

        otherUser = new UserEntity();
        otherUser.setUsername("other");
        otherUser.setPassword(passwordEncoder.encode("Pass1!"));
        Set<RoleEntity> otherRoles = new HashSet<>();
        otherRoles.add(userRole);
        otherUser.setRoles(otherRoles);
        userRepository.save(otherUser);

        ownerToken = jwtService.generateToken(ownerUser);
        otherUserToken = jwtService.generateToken(otherUser);
    }

    @Test
    void addBookToUser_shouldCreateBook() throws Exception {
        var requestBody = java.util.Map.of(
                "title", "Test Book",
                "author", "Test Author",
                "review", "Great read!"
        );

        MvcResult result = mockMvc.perform(post("/api/user/" + ownerUser.getId() + "/book")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andReturn();

        assertThat(bookRepository.findAll()).hasSize(1);
    }

    @Test
    void getUserBooks_shouldReturnUserBooks() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("My Book");
        book.setAuthor("Me");
        book.setUser(ownerUser);
        bookRepository.save(book);

        mockMvc.perform(get("/api/user/" + ownerUser.getId() + "/books")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("My Book"));
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setUser(ownerUser);
        bookRepository.save(book);

        mockMvc.perform(get("/api/books")
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book 1"))
                .andExpect(jsonPath("$[0].user.username").value("owner"));
    }

    @Test
    void getBook_shouldReturnBook() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setUser(ownerUser);
        bookRepository.save(book);

        mockMvc.perform(get("/api/books/" + book.getId())
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void editBook_shouldUpdateBook() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("Original Title");
        book.setAuthor("Original Author");
        book.setUser(ownerUser);
        bookRepository.save(book);

        var requestBody = java.util.Map.of(
                "title", "Updated Title"
        );

        mockMvc.perform(patch("/api/books/" + book.getId())
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteBook_shouldDeleteBook() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("To Delete");
        book.setAuthor("Author");
        book.setUser(ownerUser);
        bookRepository.save(book);
        Long bookId = book.getId();

        mockMvc.perform(delete("/api/books/" + bookId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    @Test
    void deleteBook_shouldRejectNonOwner() throws Exception {
        BookEntity book = new BookEntity();
        book.setTitle("Owner Book");
        book.setAuthor("Owner");
        book.setUser(ownerUser);
        bookRepository.save(book);

        mockMvc.perform(delete("/api/books/" + book.getId())
                        .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isForbidden());
    }
}
