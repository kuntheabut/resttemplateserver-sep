package com.example.resttemplateserver.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    private BookController controller;

    @BeforeEach
    void setUp() {
        controller = new BookController(bookRepository);
    }

    @Test
    void getBooksReturnsRepositoryList() {
        List<Book> books = List.of(new Book(1, "The Hobbit", "J.R.R. Tolkien", 30));
        when(bookRepository.findAll()).thenReturn(books);

        assertThat(controller.getBooks()).isSameAs(books);
        verify(bookRepository).findAll();
    }

    @Test
    void getBookReturnsBookWhenFound() {
        Book book = new Book(1, "The Hobbit", "J.R.R. Tolkien", 30);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        assertThat(controller.getBook(1)).isSameAs(book);
    }

    @Test
    void getBookThrowsNotFoundWhenMissing() {
        when(bookRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getBook(5))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Book not found: 5");
    }

    @Test
    void createBookSavesBookAndReturnsCreatedResponse() {
        Book input = new Book(100, "The Hobbit", "J.R.R. Tolkien", 30);
        Book saved = new Book(2, "The Hobbit", "J.R.R. Tolkien", 30);
        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        ResponseEntity<Book> response = controller.createBook(input);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/books/2"));
        assertThat(response.getBody()).isSameAs(saved);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void updateBookChangesFieldsWhenPresent() {
        Book existing = new Book(1, "The Hobbit", "J.R.R. Tolkien", 30);
        Book update = new Book(1, "The Lord of the Rings", "J.R.R. Tolkien", 40);

        when(bookRepository.findById(1)).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);

        Book result = controller.updateBook(1, update);

        assertThat(result.getName()).isEqualTo("The Lord of the Rings");
        assertThat(result.getAuthor()).isEqualTo("J.R.R. Tolkien");
        assertThat(result.getPrice()).isEqualTo(40);
        verify(bookRepository).save(existing);
    }

    @Test
    void updateBookThrowsNotFoundWhenMissing() {
        Book update = new Book(1, "The Lord of the Rings", "J.R.R. Tolkien", 40);
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.updateBook(1, update))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Book not found: 1");
    }

    @Test
    void deleteBookReturnsNoContentWhenExists() {
        when(bookRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteBook(1);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(bookRepository).deleteById(1);
    }

    @Test
    void deleteBookThrowsNotFoundWhenMissing() {
        when(bookRepository.existsById(5)).thenReturn(false);

        assertThatThrownBy(() -> controller.deleteBook(5))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Book not found: 5");
    }
}
