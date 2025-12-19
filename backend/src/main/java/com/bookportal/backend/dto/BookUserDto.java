package com.bookportal.backend.dto;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.userdetails.User;

import java.util.Set;

@Hidden
public class BookUserDto {

    private Long id;
    private String title;
    private String author;
    private String review;
    private UserDto user;

    public BookUserDto() {}

    public BookUserDto(Long id, String title, String author, String review, UserDto user) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.review = review;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getReview() { return review; }
    public void setReview(String review) { this.review = review; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
}
