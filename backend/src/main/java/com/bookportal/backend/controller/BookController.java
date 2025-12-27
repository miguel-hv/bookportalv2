package com.bookportal.backend.controller;

import com.bookportal.backend.dto.*;
import com.bookportal.backend.entity.BookEntity;
import com.bookportal.backend.service.BookService;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.util.SuccessMessages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Books", description = "Operations related to books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }


    @Operation(summary = "Add new book")
    @PostMapping("/user/{userId}/book")
    public BookDto addBookToUser(@PathVariable Long userId, @RequestBody BookCreateRequest request) {
        return bookService.addBookToUser(userId, request);
    }

    @Operation(summary = "Get all user books")
    @GetMapping("/user/{userId}/books")
    public List<BookDto> getUserBooks (@PathVariable Long userId) {
        return bookService.getUserBooks(userId);
    }

    @Operation(summary = "Get all books from all users")
    @GetMapping("/books")
    public List<BookUserDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Get book by id")
    @GetMapping("/books/{id}")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(summary = "Edit book")
    @PatchMapping("/books/{id}")
    @PreAuthorize("@bookSecurity.isOwner(#id, authentication.name)")
    public ResponseEntity<?> editBook(@PathVariable Long id, @RequestBody BookPatchRequest request) {
        BookEntity book = bookService.findEntityById(id);

        return ResponseEntity.ok(bookService.updateBookById(id, request));

    }

    @Operation(summary = "Delete book")
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
