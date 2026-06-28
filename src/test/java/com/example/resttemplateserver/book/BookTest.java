package com.example.resttemplateserver.book;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookTest {

    @Test
    void constructorsAndAccessorsShouldWork() {
        Book book = new Book(1, "1984", "George Orwell", 20);

        assertThat(book.getId()).isEqualTo(1);
        assertThat(book.getName()).isEqualTo("1984");
        assertThat(book.getAuthor()).isEqualTo("George Orwell");
        assertThat(book.getPrice()).isEqualTo(20);
    }

    @Test
    void settersShouldUpdateFields() {
        Book book = new Book();
        book.setId(2);
        book.setName("Dune");
        book.setAuthor("Frank Herbert");
        book.setPrice(25);

        assertThat(book.getId()).isEqualTo(2);
        assertThat(book.getName()).isEqualTo("Dune");
        assertThat(book.getAuthor()).isEqualTo("Frank Herbert");
        assertThat(book.getPrice()).isEqualTo(25);
    }
}
