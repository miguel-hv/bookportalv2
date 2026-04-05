package com.bookportal.backend.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
@Access(AccessType.FIELD)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String title;

    @Column
    private String author;

    @Column(length = 200)
    private String review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Book() {}

    public Book(String title, String author, String review) {
        this.title = title;
        this.author = author;
        this.review = review;
    }

    // Package-private factory method for testing
    static Book withTitle(String title) {
        Book book = new Book();
        book.title = title;
        return book;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getReview() { return review; }

    public User getUser() { return user; }

    @Deprecated
    public void setUser(User user) { this.user = user; }

    @Deprecated
    public void setTitle(String title) { this.title = title; }

    @Deprecated
    public void setAuthor(String author) { this.author = author; }

    @Deprecated
    public void setReview(String review) { this.review = review; }

    public void updateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (title.length() > 20) {
            throw new IllegalArgumentException("Title cannot exceed 20 characters");
        }
        this.title = title;
    }

    public void updateAuthor(String author) {
        this.author = author;
    }

    public void updateReview(String review) {
        if (review != null && review.length() > 200) {
            throw new IllegalArgumentException("Review cannot exceed 200 characters");
        }
        this.review = review;
    }

    public boolean belongsTo(Long userId) {
        return user != null && user.getId().equals(userId);
    }
}