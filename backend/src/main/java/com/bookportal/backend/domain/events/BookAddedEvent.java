package com.bookportal.backend.domain.events;

public class BookAddedEvent extends DomainEvent {
    
    private final Long bookId;
    private final Long userId;
    private final String title;
    private final String author;
    
    public BookAddedEvent(Long bookId, Long userId, String title, String author) {
        super("BookAdded");
        this.bookId = bookId;
        this.userId = userId;
        this.title = title;
        this.author = author;
    }
    
    public Long getBookId() {
        return bookId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    @Override
    public String toString() {
        return "BookAddedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", occurredAt=" + getOccurredAt() +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}