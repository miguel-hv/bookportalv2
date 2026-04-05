package com.bookportal.backend.domain.events;

public class UserRegisteredEvent extends DomainEvent {
    
    private final Long userId;
    private final String username;
    private final String role;
    
    public UserRegisteredEvent(Long userId, String username, String role) {
        super("UserRegistered");
        this.userId = userId;
        this.username = username;
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRole() {
        return role;
    }
    
    @Override
    public String toString() {
        return "UserRegisteredEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", occurredAt=" + getOccurredAt() +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}