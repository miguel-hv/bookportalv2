package com.bookportal.backend.domain.events;

public interface DomainEventListener<T extends DomainEvent> {
    
    void handle(T event);
    
    Class<T> getEventType();
}