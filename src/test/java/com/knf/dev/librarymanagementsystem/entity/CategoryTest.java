package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import org.mockito.Mock;

class CategoryTest {

    @Mock
    private Book emptyBook = new Book();

    @Mock
    private Book book1 = new Book("12345678", "Book1", "SerialBook1", "Description");

    @Mock
    private Book book2 = new Book("12345678", "Book2", "SerialBook2", "Description");

    @Property(tries = 40)
    void shouldCreateCategoryWithName(@ForAll @StringLength(max = 50) String name) {
        Category category = new Category(name);

        assertEquals(name, category.getName());
        assertNotNull(category.getBooks());
        assertTrue(category.getBooks().isEmpty());
    }

    @Example
    void shouldCreateEmptyCategory() {
        Category category = new Category();

        assertNotNull(category);
        assertNotNull(category.getBooks());
        assertTrue(category.getBooks().isEmpty());
    }

    @Property(tries = 40)
    void shouldSetAndGetId(@ForAll @LongRange(min = 0, max = Long.MAX_VALUE) long id) {
        Category category = new Category("Category");

        category.setId(id);

        assertEquals(id, category.getId());
    }

    @Property(tries = 40)
    void shouldSetAndGetName(@ForAll @StringLength(max = 50) String name) {
        Category category = new Category();

        category.setName(name);

        assertEquals(name, category.getName());
    }

    @Property(tries = 40)
    void shouldAllowChangingBooks(
            @ForAll @StringLength(min = 1, max = 50) String initialName,
            @ForAll @StringLength(min = 1, max = 50) String firstName,
            @ForAll @StringLength(min = 1, max = 50) String secondName) {
        Category category = new Category("Category");
        category.setBooks(Set.of(new Book("isbn-0", initialName, "serial-0", "description")));
        Set<Book> books = new HashSet<>();
        books.add(new Book("isbn-1", firstName, "serial-1", "description"));
        books.add(new Book("isbn-2", secondName, "serial-2", "description"));

        category.setBooks(books);

        assertEquals(books, category.getBooks());
    }

    @Property(tries = 40)
    void shouldSetAndGetEmptyBooks(@ForAll @StringLength(max = 50) String name) {
        Category category = new Category(name);
        Set<Book> books = new HashSet<>();
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertTrue(category.getBooks().isEmpty());
    }

    @Property(tries = 40)
    void shouldSetAndGetOneBook(@ForAll @StringLength(min = 1, max = 50) String name) {
        Category category = new Category("Category");
        Set<Book> books = new HashSet<>();
        books.add(new Book("isbn-1", name, "serial-1", "description"));
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertEquals(1, category.getBooks().size());
    }

    @Property(tries = 40)
    void shouldSetAndGetTwoBooks(
            @ForAll @StringLength(min = 1, max = 50) String firstName,
            @ForAll @StringLength(min = 1, max = 50) String secondName) {
        Category category = new Category("Category");
        Set<Book> books = new HashSet<>();
        books.add(new Book("isbn-1", firstName, "serial-1", "description"));
        books.add(new Book("isbn-2", secondName, "serial-2", "description"));
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertEquals(2, category.getBooks().size());
    }

    @Property(tries = 40)
    void shouldSetAndGetThreeBooks(
            @ForAll @StringLength(min = 1, max = 50) String firstName,
            @ForAll @StringLength(min = 1, max = 50) String secondName,
            @ForAll @StringLength(min = 1, max = 50) String thirdName) {
        Category category = new Category("Category");
        Set<Book> books = new HashSet<>();
        books.add(new Book("isbn-1", firstName, "serial-1", "description"));
        books.add(new Book("isbn-2", secondName, "serial-2", "description"));
        books.add(new Book("isbn-3", thirdName, "serial-3", "description"));
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertEquals(3, category.getBooks().size());
    }

    @Property(tries = 40)
    void shouldSetAndGetSameBooks(@ForAll @StringLength(min = 1, max = 50) String name) {
        Category category = new Category("Category");
        Book book = new Book("isbn-1", name, "serial-1", "description");
        Set<Book> books = new HashSet<>();
        books.add(book);
        books.add(book);
        books.add(book);
        category.setBooks(books);

        assertEquals(books, category.getBooks());
        assertEquals(1, category.getBooks().size());
    }
}
