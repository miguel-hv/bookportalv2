package com.bookportal.backend.domain.model;

import com.bookportal.backend.domain.model.enums.ERole;
import com.bookportal.backend.util.ErrorMessages;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Book> books = new HashSet<>();

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static User create(String username, String password, Set<Role> roles) {
        User user = new User(username, password);
        user.roles = roles;
        return user;
    }

    // Package-private factory for testing
    static User withUsername(String username) {
        User user = new User();
        user.username = username;
        return user;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public Set<Role> getRoles() { return roles; }

    public Set<Book> getBooks() { return books; }

    // Deprecated - use domain methods or factory methods instead
    @Deprecated
    public void setUsername(String username) { this.username = username; }

    @Deprecated
    public void setPassword(String password) { this.password = password; }

    @Deprecated
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
    }

    public boolean hasRole(ERole role) {
        return roles.stream().anyMatch(r -> r.getName() == role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getName().name().equals(roleName));
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void addBook(Book book) {
        books.add(book);
        book.setUser(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setUser(null);
    }

    public boolean isOwnerOf(Book book) {
        return book.getUser() != null && book.getUser().getId().equals(this.id);
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Password cannot be empty");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalStateException(ErrorMessages.NO_ROLES_FOUND.getMessage());
        }
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}