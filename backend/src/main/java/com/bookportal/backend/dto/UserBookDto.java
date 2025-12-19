package com.bookportal.backend.dto;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.Set;

@Hidden
public class UserBookDto {

    private Long id;
    private String username;
    private Set<String> role;
    private  Set<BookDto> books;

    public UserBookDto() {}

    public UserBookDto(Long id, String username,  Set<String> role, Set<BookDto> books) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.books = books;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public   Set<String> getRole() { return role; }
    public void setRole( Set<String> role) { this.role = role; }

    public Set<BookDto> getBooks() { return books; }
    public void setBooks(Set<BookDto> books) { this.books = books; }
}
