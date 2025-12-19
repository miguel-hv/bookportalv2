package com.bookportal.backend.controller;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.service.BookService;
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
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
