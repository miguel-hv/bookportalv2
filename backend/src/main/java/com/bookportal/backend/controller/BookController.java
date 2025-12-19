package com.bookportal.backend.controller;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.entity.BookEntity;
import com.bookportal.backend.service.BookService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?>  deleteBook(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(ErrorMessages.NOT_ALLOWED_ROLE.getMessage()));
        }

        BookEntity book = bookService.findEntityById(id);

        String username = authentication.getName();

        boolean isOwner = book.getUser().getUsername().equals(username);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(ErrorMessages.NOT_ALLOWED_ROLE.getMessage()));
        }

        bookService.deleteBook(id);

        return ResponseEntity.ok(
                new MessageResponse(SuccessMessages.BOOK_DELETED.getMessage())
        );
    }
}
