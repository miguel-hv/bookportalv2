package com.bookportal.backend.service;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookPatchRequest;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.domain.model.Book;
import com.bookportal.backend.domain.model.User;
import com.bookportal.backend.exception.ResourceNotFoundException;
import com.bookportal.backend.application.mapper.BookMapper;
import com.bookportal.backend.infrastructure.events.DomainEventDispatcher;
import com.bookportal.backend.infrastructure.repository.BookRepository;
import com.bookportal.backend.infrastructure.repository.UserRepository;
import com.bookportal.backend.util.ErrorMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DomainEventDispatcher eventDispatcher;

    @InjectMocks
    private BookService bookService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john");

        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setReview("Great book!");
        book.setUser(user);
    }

    @Test
    void addBookToUser_shouldCreateBook() {
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle("New Book");
        request.setAuthor("Author");
        request.setReview("Review");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            return saved;
        });

        BookDto result = bookService.addBookToUser(1L, request);

        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        assertEquals("Author", result.getAuthor());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBookToUser_shouldThrowWhenUserNotFound() {
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle("New Book");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.addBookToUser(999L, request)
        );

        assertEquals(ErrorMessages.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void getUserBooks_shouldReturnBooksForUser() {
        when(bookRepository.findByUserId(1L)).thenReturn(List.of(book));

        List<BookDto> result = bookService.getUserBooks(1L);

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
    }

    @Test
    void getAllBooks_shouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookUserDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        assertEquals("john", result.get(0).getUser().getUsername());
    }

    @Test
    void getBookById_shouldReturnBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDto result = bookService.getBookById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void getBookById_shouldThrowWhenNotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.getBookById(999L)
        );

        assertEquals(ErrorMessages.BOOK_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void findEntityById_shouldReturnEntity() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.findEntityById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
    }

    @Test
    void updateBookById_shouldUpdateFields() {
        BookPatchRequest request = new BookPatchRequest();
        request.setTitle("Updated Title");
        request.setAuthor("Updated Author");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookDto result = bookService.updateBookById(1L, request);

        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_shouldCallRepository() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }
}
