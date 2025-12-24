package com.bookportal.backend.service;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookPatchRequest;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.entity.BookEntity;
import com.bookportal.backend.entity.UserEntity;
import com.bookportal.backend.mapper.BookMapper;
import com.bookportal.backend.repository.BookRepository;
import com.bookportal.backend.repository.UserRepository;
import com.bookportal.backend.util.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public BookDto addBookToUser(Long userId, BookCreateRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.USER_NOT_FOUND.getMessage()));

        BookEntity book = BookMapper.fromCreateRequest(request);
        book.setUser(user);

        user.getBooks().add(book);
        bookRepository.save(book);

        return BookMapper.toDto(book);
    }

    public List<BookDto> getUserBooks(Long userId) {
        return bookRepository.findByUserId(userId)
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    public List<BookUserDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toBookUserDto)
                .toList();
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));
    }

    public BookEntity findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));
    }

    public BookDto updateBookById(Long id, BookPatchRequest request) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getReview() != null) {
            book.setReview(request.getReview());
        }

        return BookMapper.toDto(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
