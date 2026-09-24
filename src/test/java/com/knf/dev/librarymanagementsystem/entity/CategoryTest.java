package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class CategoryTest {

    @Mock
    private Book emptyBook = new Book();

    @Mock
    private Book book1 = new Book("12345678", "Book1", "SerialBook1", "Description");

    @Mock
    private Book book2 = new Book("12345678", "Book2", "SerialBook2", "Description");

    @Test
    void shouldCreateCategoryWithName() {
        Category category = new Category("Category1");

        assertEquals("Category1", category.getName());
        assertNotNull(category.getBooks());
        assertTrue(category.getBooks().isEmpty());
    }

    @Test
    void shouldCreateEmptyCategory() {
        Category category = new Category();

        assertNotNull(category);
        assertNotNull(category.getBooks());
        assertTrue(category.getBooks().isEmpty());
    }

    @Test
    void shouldSetAndGetId() {
        Category category = new Category("Category2");

        category.setId(10L);

        assertEquals(10L, category.getId());
    }

    @Test
    void shouldSetAndGetName() {
        Category category = new Category();

        category.setName("Category3");

        assertEquals("Category3", category.getName());
    }

    @Test
    void shouldAllowChangingBooks() {
        Category category = new Category("Category5");

        Set<Book> books = new HashSet<>();
        books.add(book1);
        category.setBooks(books);

        Set<Book> books2 = new HashSet<>();
        books2.add(book2);
        books2.add(book1);
        category.setBooks(books2);

        assertEquals(books2, category.getBooks());
    }


    @Test
    void shouldSetAndGetBooks() {
        Category category = new Category("category6");

        Set<Book> books = new HashSet<>();
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertTrue(category.getBooks().isEmpty());

        Set<Book> books1 = new HashSet<>();
        books1.add(emptyBook);
        category.setBooks(books1);

        assertEquals(books1, category.getBooks());
        assertEquals(1, category.getBooks().size());

        Set<Book> books2 = new HashSet<>();
        books2.add(emptyBook);
        books2.add(book1);
        category.setBooks(books2);

        assertEquals(books2, category.getBooks());
        assertEquals(2, category.getBooks().size());

        Set<Book> books3 = new HashSet<>();
        books3.add(emptyBook);
        books3.add(book1);
        books3.add(book2);
        category.setBooks(books3);

        assertEquals(books3, category.getBooks());
        assertEquals(3, category.getBooks().size());
    }

    //Comprobamos que no se añadan libros iguales teniendo en cuenta que los mocks son con distinto hash
    @Test
    void shouldSetAndGetSameBooks(){
        Category category = new Category("Category");
        Set<Book> sameBooks = new HashSet<>();
        sameBooks.add(book1);
        sameBooks.add(book1);
        sameBooks.add(book1);
        category.setBooks(sameBooks);

        assertEquals(sameBooks, category.getBooks());
        assertEquals(1, category.getBooks().size());
    }

}

