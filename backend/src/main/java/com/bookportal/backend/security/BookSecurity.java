package com.bookportal.backend.security;

import com.bookportal.backend.repository.BookRepository;
import org.springframework.stereotype.Component;

@Component("bookSecurity")
public class BookSecurity {

    private final BookRepository bookRepository;

    public BookSecurity(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public boolean isOwner(Long bookId, String username) {
        return bookRepository.findById(bookId)
                .map(book -> book.getUser().getUsername().equals(username))
                .orElse(false);
    }
}

