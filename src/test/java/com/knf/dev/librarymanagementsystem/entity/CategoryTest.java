package com.knf.dev.librarymanagementsystem.entity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class CategoryTest {

    private final Book emptyBook = new Book();
    private final Book book1 = new Book("12345678", "Book1", "SerialBook1", "Description");
    private final Book book2 = new Book("87654321", "Book2", "SerialBook2", "Description");

    @Test
    void shouldCreateCategoryWithName_guardaName() {
        Category category = new Category("Category1");

        assertAll(
                "Comprobar que se crea una categoría con el nombre dado",
                () -> assertEquals("Category1", category.getName())
        );
    }

    @Test
    void shouldCreateCategoryWithName_booksNotNull() {
        Category category = new Category("Category1");

        assertAll(
                "Comprobar que la colección de libros no es null",
                () -> assertNotNull(category.getBooks())
        );
    }

    @Test
    void shouldCreateCategoryWithName_booksVacio() {
        Category category = new Category("Category1");

        assertAll(
                "Comprobar que la colección de libros empieza vacía",
                () -> assertTrue(category.getBooks().isEmpty())
        );
    }

    @Test
    void shouldCreateEmptyCategory_instanciaNotNull() {
        Category category = new Category();

        assertAll(
                "Comprobar que se crea la instancia",
                () -> assertNotNull(category)
        );
    }

    @Test
    void shouldCreateEmptyCategory_booksNotNull() {
        Category category = new Category();

        assertAll(
                "Comprobar que books no es null en categoría vacía",
                () -> assertNotNull(category.getBooks())
        );
    }

    @Test
    void shouldCreateEmptyCategory_booksVacio() {
        Category category = new Category();

        assertAll(
                "Comprobar que books está vacío en categoría vacía",
                () -> assertTrue(category.getBooks().isEmpty())
        );
    }

    @Test
    void shouldSetAndGetId() {
        Category category = new Category("Category2");

        category.setId(10L);

        assertAll(
                "Comprobar que se puede establecer y obtener el ID de una categoría",
                () -> assertEquals(10L, category.getId())
        );
    }

    @Test
    void shouldSetAndGetName() {
        Category category = new Category();

        category.setName("Category3");

        assertAll(
                "Comprobar que se puede establecer y obtener el nombre de una categoría",
                () -> assertEquals("Category3", category.getName())
        );
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

        assertAll(
                "Comprobar que se puede modificar el conjunto de libros de una categoría",
                () -> assertEquals(books2, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetEmptyBooks_mismaReferencia() {
        Category category = new Category("category6");

        Set<Book> books = new HashSet<>();
        category.setBooks(books);

        assertAll(
                "Comprobar que setBooks guarda el conjunto vacío indicado",
                () -> assertEquals(books, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetEmptyBooks_estaVacio() {
        Category category = new Category("category6");

        Set<Book> books = new HashSet<>();
        category.setBooks(books);

        assertAll(
                "Comprobar que el conjunto de libros está vacío",
                () -> assertTrue(category.getBooks().isEmpty())
        );
    }

    @Test
    void shouldSetAndGetOneBook_mismaColeccion() {
        Category category = new Category("category6");

        Set<Book> books1 = new HashSet<>();
        books1.add(emptyBook);
        category.setBooks(books1);

        assertAll(
                "Comprobar que getBooks devuelve el conjunto con un libro",
                () -> assertEquals(books1, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetOneBook_tamanoUno() {
        Category category = new Category("category6");

        Set<Book> books1 = new HashSet<>();
        books1.add(emptyBook);
        category.setBooks(books1);

        assertAll(
                "Comprobar que una categoría puede contener exactamente un libro",
                () -> assertEquals(1, category.getBooks().size())
        );
    }

    @Test
    void shouldSetAndGetTwoBooks_mismaColeccion() {
        Category category = new Category("category6");

        Set<Book> books2 = new HashSet<>();
        books2.add(emptyBook);
        books2.add(book1);
        category.setBooks(books2);

        assertAll(
                "Comprobar que getBooks devuelve el conjunto con dos libros",
                () -> assertEquals(books2, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetTwoBooks_tamanoDos() {
        Category category = new Category("category6");

        Set<Book> books2 = new HashSet<>();
        books2.add(emptyBook);
        books2.add(book1);
        category.setBooks(books2);

        assertAll(
                "Comprobar que una categoría puede contener exactamente dos libros",
                () -> assertEquals(2, category.getBooks().size())
        );
    }

    @Test
    void shouldSetAndGetThreeBooks_mismaColeccion() {
        Category category = new Category("category6");

        Set<Book> books3 = new HashSet<>();
        books3.add(emptyBook);
        books3.add(book1);
        books3.add(book2);
        category.setBooks(books3);

        assertAll(
                "Comprobar que getBooks devuelve el conjunto con tres libros",
                () -> assertEquals(books3, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetThreeBooks_tamanoTres() {
        Category category = new Category("category6");

        Set<Book> books3 = new HashSet<>();
        books3.add(emptyBook);
        books3.add(book1);
        books3.add(book2);
        category.setBooks(books3);

        assertAll(
                "Comprobar que una categoría puede contener exactamente tres libros",
                () -> assertEquals(3, category.getBooks().size())
        );
    }

    @Test
    void shouldSetAndGetSameBooks_mismaColeccion() {
        Category category = new Category("Category");

        Set<Book> sameBooks = new HashSet<>();
        sameBooks.add(book1);
        sameBooks.add(book1);
        sameBooks.add(book1);
        category.setBooks(sameBooks);

        assertAll(
                "Comprobar que getBooks devuelve el set con la instancia única",
                () -> assertEquals(sameBooks, category.getBooks())
        );
    }

    @Test
    void shouldSetAndGetSameBooks_tamanoUno() {
        Category category = new Category("Category");

        Set<Book> sameBooks = new HashSet<>();
        sameBooks.add(book1);
        sameBooks.add(book1);
        sameBooks.add(book1);
        category.setBooks(sameBooks);

        assertAll(
                "Comprobar que no se almacenan varias veces la misma instancia de un libro",
                () -> assertEquals(1, category.getBooks().size())
        );
    }

}
