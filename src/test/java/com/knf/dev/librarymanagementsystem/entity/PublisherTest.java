package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;


class PublisherTest {

  private final Book book1 = new Book("111", "Book1", "SerialBook1", "Description");
  private final Book book2 = new Book("222", "Book2", "SerialBook2", "Description");
  private final Book book3 = new Book("333", "Book3", "SerialBook3", "Description");

  static Stream<String> nombresFrontera() {
    return Stream.of("", "a", "a".repeat(99), "a".repeat(100), "a".repeat(101));
  }


  @Test
  void shouldCreateEmptyPublisher_idNull() {
    Publisher publisher = new Publisher();

    assertAll(
        "Comprobar que el constructor vacío deja el id a null",
        () -> assertNull(publisher.getId())
    );
  }

  @Test
  void shouldCreateEmptyPublisher_nameNull() {
    Publisher publisher = new Publisher();

    assertAll(
        "Comprobar que el constructor vacío deja el name a null",
        () -> assertNull(publisher.getName())
    );
  }

  @Test
  void shouldCreateEmptyPublisher_booksNotNull() {
    Publisher publisher = new Publisher();

    assertAll(
        "Comprobar que el constructor vacío inicializa books",
        () -> assertNotNull(publisher.getBooks())
    );
  }

  @Test
  void shouldCreateEmptyPublisher_booksEmpty() {
    Publisher publisher = new Publisher();

    assertAll(
        "Comprobar que el constructor vacío deja books vacío",
        () -> assertTrue(publisher.getBooks().isEmpty())
    );
  }

  @Test
  void shouldCreatePublisherWithName_nameStored() {
    Publisher publisher = new Publisher("Pearson");

    assertAll(
        "Comprobar que el constructor con nombre guarda el name",
        () -> assertEquals("Pearson", publisher.getName())
    );
  }

  @Test
  void shouldCreatePublisherWithName_idNull() {
    Publisher publisher = new Publisher("Pearson");

    assertAll(
        "Comprobar que el constructor con nombre deja el id a null",
        () -> assertNull(publisher.getId())
    );
  }

  @Test
  void shouldCreatePublisherWithName_booksNotNull() {
    Publisher publisher = new Publisher("Pearson");

    assertAll(
        "Comprobar que el constructor con nombre inicializa books",
        () -> assertNotNull(publisher.getBooks())
    );
  }

  @Test
  void shouldCreatePublisherWithName_booksEmpty() {
    Publisher publisher = new Publisher("Pearson");

    assertAll(
        "Comprobar que el constructor con nombre deja books vacío",
        () -> assertTrue(publisher.getBooks().isEmpty())
    );
  }

  @ParameterizedTest
  @MethodSource("nombresFrontera")
  void shouldCreatePublisherWithBoundaryName(String name) {
    Publisher publisher = new Publisher(name);

    assertAll(
        "Comprobar que el constructor guarda nombres en la frontera de longitud (0, 1, 99, 100, 101)",
        () -> assertEquals(name, publisher.getName())
    );
  }

  @Test
  void shouldCreatePublisherWithNullName() {
    Publisher publisher = new Publisher(null);

    assertAll(
        "Comprobar que el constructor acepta un nombre null",
        () -> assertNull(publisher.getName())
    );
  }

  @Test
  void shouldNotShareBooksBetweenPublishers() {
    Publisher publisher1 = new Publisher("Pearson");
    Publisher publisher2 = new Publisher("Anaya");

    assertAll(
        "Comprobar que cada editorial tiene su propio conjunto de libros",
        () -> assertNotSame(publisher1.getBooks(), publisher2.getBooks())
    );
  }


  @ParameterizedTest
  @ValueSource(longs = { Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE })
  void shouldSetAndGetId(long id) {
    Publisher publisher = new Publisher("Pearson");

    publisher.setId(id);

    assertAll(
        "Comprobar que se puede establecer y obtener el ID en sus fronteras",
        () -> assertEquals(id, publisher.getId())
    );
  }

  @Test
  void shouldSetIdToNull() {
    Publisher publisher = new Publisher("Pearson");
    publisher.setId(5L);

    publisher.setId(null);

    assertAll(
        "Comprobar que se puede volver a poner el ID a null",
        () -> assertNull(publisher.getId())
    );
  }


  @ParameterizedTest
  @MethodSource("nombresFrontera")
  void shouldSetAndGetBoundaryName(String name) {
    Publisher publisher = new Publisher();

    publisher.setName(name);

    assertAll(
        "Comprobar que setName guarda nombres en la frontera de longitud (0, 1, 99, 100, 101)",
        () -> assertEquals(name, publisher.getName())
    );
  }

  @Test
  void shouldChangeName() {
    Publisher publisher = new Publisher("Pearson");

    publisher.setName("Anaya");

    assertAll(
        "Comprobar que setName sustituye el nombre anterior",
        () -> assertEquals("Anaya", publisher.getName())
    );
  }

  @Test
  void shouldSetNameToNull() {
    Publisher publisher = new Publisher("Pearson");

    publisher.setName(null);

    assertAll(
        "Comprobar que se puede poner el nombre a null",
        () -> assertNull(publisher.getName())
    );
  }


  @Test
  void shouldSetAndGetEmptyBooks_equalsSet() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>();

    publisher.setBooks(books);

    assertAll(
        "Comprobar que se puede establecer y obtener un conjunto de libros vacío",
        () -> assertEquals(books, publisher.getBooks())
    );
  }

  @Test
  void shouldSetAndGetEmptyBooks_isEmpty() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>();

    publisher.setBooks(books);

    assertAll(
        "Comprobar que el conjunto de libros establecido vacío permanece vacío",
        () -> assertTrue(publisher.getBooks().isEmpty())
    );
  }

  @Test
  void shouldSetAndGetOneBook_equalsSet() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial guarda el conjunto con un libro",
        () -> assertEquals(books, publisher.getBooks())
    );
  }

  @Test
  void shouldSetAndGetOneBook_size() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial puede contener exactamente un libro",
        () -> assertEquals(1, publisher.getBooks().size())
    );
  }

  @Test
  void shouldSetAndGetTwoBooks_equalsSet() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1, book2));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial guarda el conjunto con dos libros",
        () -> assertEquals(books, publisher.getBooks())
    );
  }

  @Test
  void shouldSetAndGetTwoBooks_size() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1, book2));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial puede contener exactamente dos libros",
        () -> assertEquals(2, publisher.getBooks().size())
    );
  }

  @Test
  void shouldSetAndGetThreeBooks_equalsSet() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1, book2, book3));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial guarda el conjunto con tres libros",
        () -> assertEquals(books, publisher.getBooks())
    );
  }

  @Test
  void shouldSetAndGetThreeBooks_size() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1, book2, book3));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que una editorial puede contener exactamente tres libros",
        () -> assertEquals(3, publisher.getBooks().size())
    );
  }

  @Test
  void shouldNotStoreSameBookTwice() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>();
    books.add(book1);
    books.add(book1);
    books.add(book1);

    publisher.setBooks(books);

    assertAll(
        "Comprobar que no se almacena varias veces la misma instancia de un libro",
        () -> assertEquals(1, publisher.getBooks().size())
    );
  }

  @Test
  void shouldKeepSameSetInstance() {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>(Set.of(book1));

    publisher.setBooks(books);

    assertAll(
        "Comprobar que setBooks guarda la misma instancia del conjunto, sin copiarla",
        () -> assertSame(books, publisher.getBooks())
    );
  }

  @Test
  void shouldReplacePreviousBooks() {
    Publisher publisher = new Publisher("Pearson");
    publisher.setBooks(new HashSet<>(Set.of(book1)));
    Set<Book> newBooks = new HashSet<>(Set.of(book2, book3));

    publisher.setBooks(newBooks);

    assertAll(
        "Comprobar que setBooks sustituye el conjunto anterior",
        () -> assertEquals(newBooks, publisher.getBooks())
    );
  }

  @Test
  void shouldSetBooksToNull() {
    Publisher publisher = new Publisher("Pearson");

    publisher.setBooks(null);

    assertAll(
        "Comprobar que se puede poner el conjunto de libros a null",
        () -> assertNull(publisher.getBooks())
    );
  }
}
