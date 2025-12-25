package com.bookportal.backend.controller;

import com.bookportal.backend.dto.*;
import com.bookportal.backend.entity.BookEntity;
import com.bookportal.backend.service.BookService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/user/{userId}/books")
    public BookDto addBookToUser(@PathVariable Long userId, @RequestBody BookCreateRequest request) {
        return bookService.addBookToUser(userId, request);
    }

    @GetMapping("/user/{userId}/books")
    public List<BookDto> getUserBooks (@PathVariable Long userId) {
        return bookService.getUserBooks(userId);
    }

    @GetMapping("/books")
    public List<BookUserDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PatchMapping("/books/{id}")
    @PreAuthorize("@bookSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> editBook(@PathVariable Long id, @RequestBody BookPatchRequest request) {
        BookEntity book = bookService.findEntityById(id);

        return ResponseEntity.ok(bookService.updateBookById(id, request));

    }

    @DeleteMapping("/books/{id}")
    @PreAuthorize(
            "@bookSecurity.isOwner(#id, authentication.name) or hasRole('ADMIN')"
    )
    public ResponseEntity<?>  deleteBook(@PathVariable Long id, Authentication authentication) {
        bookService.deleteBook(id);

        return ResponseEntity.ok(
                new MessageResponse(SuccessMessages.BOOK_DELETED.getMessage())
        );
    }
}
