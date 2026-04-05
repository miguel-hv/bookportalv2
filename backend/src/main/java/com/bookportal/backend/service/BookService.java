package com.bookportal.backend.service;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookPatchRequest;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.application.mapper.BookMapper;
import com.bookportal.backend.domain.events.BookAddedEvent;
import com.bookportal.backend.domain.model.Book;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.infrastructure.events.DomainEventDispatcher;
import com.bookportal.backend.infrastructure.repository.BookRepository;
import com.bookportal.backend.infrastructure.repository.UserRepository;
import com.bookportal.backend.util.ErrorMessages;
import com.bookportal.backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final DomainEventDispatcher eventDispatcher;

    public BookService(BookRepository bookRepository, UserRepository userRepository, DomainEventDispatcher eventDispatcher) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @Transactional
    public BookDto addBookToUser(Long userId, BookCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND.getMessage()));

        Book book = BookMapper.fromCreateRequest(request);
        user.addBook(book);
        bookRepository.save(book);

        eventDispatcher.publish(new BookAddedEvent(
                book.getId(),
                userId,
                book.getTitle(),
                book.getAuthor()
        ));

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
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));
    }

    public Book findEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));
    }

    @Transactional
    public BookDto updateBookById(Long id, BookPatchRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ErrorMessages.BOOK_NOT_FOUND.getMessage()));

        if (request.getTitle() != null) {
            book.updateTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.updateAuthor(request.getAuthor());
        }
        if (request.getReview() != null) {
            book.updateReview(request.getReview());
        }

        return BookMapper.toDto(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}