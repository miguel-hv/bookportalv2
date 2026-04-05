package com.bookportal.backend.infrastructure.events;

import com.bookportal.backend.domain.events.DomainEvent;
import com.bookportal.backend.domain.events.DomainEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DomainEventDispatcher {
    
    private static final Logger log = LoggerFactory.getLogger(DomainEventDispatcher.class);
    
    private final Map<Class<? extends DomainEvent>, List<DomainEventListener<?>>> listeners = new ConcurrentHashMap<>();
    
    public <T extends DomainEvent> void register(DomainEventListener<T> listener) {
        Class<T> eventType = listener.getEventType();
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
        log.debug("Registered listener {} for event type {}", listener.getClass().getSimpleName(), eventType.getSimpleName());
    }
    
    public void publish(DomainEvent event) {
        List<DomainEventListener<?>> eventListeners = listeners.get(event.getClass());
        
        if (eventListeners == null || eventListeners.isEmpty()) {
            log.debug("No listeners for event: {}", event.getEventType());
            return;
        }
        
        log.info("Publishing event: {} with {} listener(s)", event.getEventType(), eventListeners.size());
        
        for (DomainEventListener<?> listener : eventListeners) {
            try {
                @SuppressWarnings("unchecked")
                DomainEventListener<DomainEvent> typedListener = (DomainEventListener<DomainEvent>) listener;
                typedListener.handle(event);
            } catch (Exception e) {
                log.error("Error handling event {} with listener {}", event.getEventType(), listener.getClass().getSimpleName(), e);
            }
        }
    }
    
    public void clear() {
        listeners.clear();
    }
    
    public List<DomainEventListener<?>> getListeners(Class<? extends DomainEvent> eventType) {
        return new ArrayList<>(listeners.getOrDefault(eventType, List.of()));
    }
}