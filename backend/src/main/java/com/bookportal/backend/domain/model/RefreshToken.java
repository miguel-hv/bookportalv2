package com.bookportal.backend.domain.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Access(AccessType.FIELD)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshToken() {}

    public RefreshToken(User user, String token, Instant expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public Long getId() { return id; }

    public String getToken() { return token; }

    @Deprecated
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    
    @Deprecated
    public void setUser(User user) { this.user = user; }

    public Instant getExpiryDate() { return expiryDate; }

    @Deprecated
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public boolean isValid() {
        return !isExpired() && user != null;
    }
}