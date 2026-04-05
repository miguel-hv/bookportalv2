package com.bookportal.backend.infrastructure.events;

import com.bookportal.backend.domain.events.BookAddedEvent;
import com.bookportal.backend.domain.events.UserRegisteredEvent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventInitializer {
    
    private static final Logger log = LoggerFactory.getLogger(EventInitializer.class);
    
    private final DomainEventDispatcher dispatcher;
    private final AuditEventListener auditEventListener;
    
    public EventInitializer(DomainEventDispatcher dispatcher, AuditEventListener auditEventListener) {
        this.dispatcher = dispatcher;
        this.auditEventListener = auditEventListener;
    }
    
    @PostConstruct
    public void registerListeners() {
        log.info("Registering domain event listeners...");
        
        dispatcher.register(auditEventListener);
        
        log.info("Domain event listeners registered successfully");
    }
}