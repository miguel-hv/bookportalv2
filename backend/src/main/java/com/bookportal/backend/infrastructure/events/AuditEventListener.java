package com.bookportal.backend.infrastructure.events;

import com.bookportal.backend.domain.events.DomainEvent;
import com.bookportal.backend.domain.events.DomainEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener implements DomainEventListener<DomainEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);
    
    @Override
    public void handle(DomainEvent event) {
        log.info("[AUDIT] Event occurred: {}", event);
    }
    
    @Override
    public Class<DomainEvent> getEventType() {
        return DomainEvent.class;
    }
}