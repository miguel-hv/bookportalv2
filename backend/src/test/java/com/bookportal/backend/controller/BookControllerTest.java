package com.bookportal.backend.controller;

import com.bookportal.backend.dto.BookCreateRequest;
import com.bookportal.backend.dto.BookDto;
import com.bookportal.backend.dto.BookPatchRequest;
import com.bookportal.backend.dto.BookUserDto;
import com.bookportal.backend.dto.MessageResponse;
import com.bookportal.backend.domain.model.Book;
import com.bookportal.backend.service.BookService;
import com.bookportal.backend.util.SuccessMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController controller;

    private BookDto bookDto;
    private BookUserDto bookUserDto;
    private Book bookEntity;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        bookDto = new BookDto(1L, "Test Book", "Test Author", "Great!");
        bookUserDto = new BookUserDto(1L, "Test Book", "Test Author", "Great!", null);
        bookEntity = new Book();
        bookEntity.setTitle("Test Book");
        authentication = mock(Authentication.class);
    }

    @Test
    void addBookToUser_shouldReturnBook() {
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle("New Book");
        request.setAuthor("Author");

        when(bookService.addBookToUser(eq(1L), any(BookCreateRequest.class))).thenReturn(bookDto);

        BookDto result = controller.addBookToUser(1L, request);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookService).addBookToUser(eq(1L), any(BookCreateRequest.class));
    }

    @Test
    void getUserBooks_shouldReturnList() {
        when(bookService.getUserBooks(1L)).thenReturn(List.of(bookDto));

        List<BookDto> result = controller.getUserBooks(1L);

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookService).getUserBooks(1L);
    }

    @Test
    void getAllBooks_shouldReturnList() {
        when(bookService.getAllBooks()).thenReturn(List.of(bookUserDto));

        List<BookUserDto> result = controller.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookService).getAllBooks();
    }

    @Test
    void getBook_shouldReturnBook() {
        when(bookService.getBookById(1L)).thenReturn(bookDto);

        BookDto result = controller.getBook(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookService).getBookById(1L);
    }

    @Test
    void editBook_shouldUpdateAndReturn() {
        BookPatchRequest request = new BookPatchRequest();
        request.setTitle("Updated Title");

        when(bookService.findEntityById(1L)).thenReturn(bookEntity);
        when(bookService.updateBookById(eq(1L), any(BookPatchRequest.class))).thenReturn(bookDto);

        ResponseEntity<?> result = controller.editBook(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(bookService).updateBookById(eq(1L), any(BookPatchRequest.class));
    }

    @Test
    void deleteBook_shouldReturnSuccessMessage() {
        doNothing().when(bookService).deleteBook(1L);

        ResponseEntity<?> result = controller.deleteBook(1L, authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof MessageResponse);
        assertEquals(SuccessMessages.BOOK_DELETED.getMessage(), 
                ((MessageResponse) result.getBody()).message());
        verify(bookService).deleteBook(1L);
    }
}
