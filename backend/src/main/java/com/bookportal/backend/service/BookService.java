package com.bookportal.backend.service;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
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

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(BookMapper::toDto)
                .toList();
    }

    public BookDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toDto)
                .orElseThrow(() -> new RuntimeException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
