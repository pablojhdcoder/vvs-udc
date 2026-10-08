package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;

/** Datos aleatorios (jqwik) para las operaciones públicas de Book. Un assert por propiedad. */
class BookProperties {

  @Property(tries = 40)
  void constructorConservaIsbn(@ForAll @StringLength(max = 50) String isbn) {
    Book book = new Book(isbn, "nome", "serial", "desc");

    assertEquals(isbn, book.getIsbn());
  }

  @Property(tries = 40)
  void constructorConservaNome(@ForAll @StringLength(max = 100) String name) {
    Book book = new Book("isbn", name, "serial", "desc");

    assertEquals(name, book.getName());
  }

  @Property(tries = 40)
  void constructorConservaSerial(@ForAll @StringLength(max = 50) String serial) {
    Book book = new Book("isbn", "nome", serial, "desc");

    assertEquals(serial, book.getSerialName());
  }

  @Property(tries = 40)
  void constructorConservaDescricion(@ForAll @StringLength(max = 250) String description) {
    Book book = new Book("isbn", "nome", "serial", description);

    assertEquals(description, book.getDescription());
  }

  @Property(tries = 30)
  void constructorDeixaAuthorsBaleiro(
      @ForAll @StringLength(max = 20) String isbn, @ForAll @StringLength(max = 20) String name) {
    Book book = new Book(isbn, name, "serial", "desc");

    assertTrue(book.getAuthors().isEmpty());
  }

  @Property(tries = 30)
  void setIsbnConservaValor(@ForAll @StringLength(max = 50) String isbn) {
    Book book = new Book();
    book.setIsbn(isbn);

    assertEquals(isbn, book.getIsbn());
  }

  @Property(tries = 30)
  void setNameConservaValor(@ForAll @StringLength(max = 100) String name) {
    Book book = new Book();
    book.setName(name);

    assertEquals(name, book.getName());
  }

  @Property(tries = 20)
  void addAuthorsIdempotenteNoTamano(@ForAll @StringLength(min = 1, max = 30) String nomeAutor) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Author author = new Author(nomeAutor, "desc");
    book.addAuthors(author);

    book.addAuthors(author);

    assertEquals(1, book.getAuthors().size());
  }

  @Property(tries = 20)
  void addCategoriesIdempotenteNoTamano(@ForAll @StringLength(min = 1, max = 30) String nome) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Category category = new Category(nome);
    book.addCategories(category);

    book.addCategories(category);

    assertEquals(1, book.getCategories().size());
  }

  @Property(tries = 20)
  void addPublishersIdempotenteNoTamano(@ForAll @StringLength(min = 1, max = 30) String nome) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Publisher publisher = new Publisher(nome);
    book.addPublishers(publisher);

    book.addPublishers(publisher);

    assertEquals(1, book.getPublishers().size());
  }

  @Property(tries = 20)
  void removeAuthorsDeixaVacio(@ForAll @StringLength(min = 1, max = 30) String nomeAutor) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Author author = new Author(nomeAutor, "desc");
    book.addAuthors(author);

    book.removeAuthors(author);

    assertTrue(book.getAuthors().isEmpty());
  }

  @Property(tries = 20)
  void removeCategoriesDeixaVacio(@ForAll @StringLength(min = 1, max = 30) String nome) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Category category = new Category(nome);
    book.addCategories(category);

    book.removeCategories(category);

    assertTrue(book.getCategories().isEmpty());
  }

  @Property(tries = 20)
  void removePublishersDeixaVacio(@ForAll @StringLength(min = 1, max = 30) String nome) {
    Book book = new Book("isbn", "nome", "serial", "desc");
    Publisher publisher = new Publisher(nome);
    book.addPublishers(publisher);

    book.removePublishers(publisher);

    assertTrue(book.getPublishers().isEmpty());
  }

  @Property(tries = 15)
  void isbnBaleiro(@ForAll @StringLength(min = 0, max = 0) String isbn) {
    assertEquals(isbn, new Book(isbn, "nome", "serial", "desc").getIsbn());
  }

  @Property(tries = 15)
  void isbnNaLonxitudeDaColumna(@ForAll @StringLength(min = 50, max = 50) String isbn) {
    assertEquals(isbn, new Book(isbn, "nome", "serial", "desc").getIsbn());
  }

  @Property(tries = 15)
  void isbnUnCaracterPorRibaDaColumna(@ForAll @StringLength(min = 51, max = 51) String isbn) {
    assertEquals(isbn, new Book(isbn, "nome", "serial", "desc").getIsbn());
  }

  @Property(tries = 15)
  void nomeNaLonxitudeDaColumna(@ForAll @StringLength(min = 100, max = 100) String name) {
    assertEquals(name, new Book("isbn", name, "serial", "desc").getName());
  }

  @Property(tries = 15)
  void nomeUnCaracterPorRibaDaColumna(@ForAll @StringLength(min = 101, max = 101) String name) {
    assertEquals(name, new Book("isbn", name, "serial", "desc").getName());
  }

  @Property(tries = 15)
  void serialNaLonxitudeDaColumna(@ForAll @StringLength(min = 50, max = 50) String serial) {
    assertEquals(serial, new Book("isbn", "nome", serial, "desc").getSerialName());
  }

  @Property(tries = 15)
  void serialUnCaracterPorRibaDaColumna(@ForAll @StringLength(min = 51, max = 51) String serial) {
    assertEquals(serial, new Book("isbn", "nome", serial, "desc").getSerialName());
  }

  @Property(tries = 10)
  void descricionNaLonxitudeDaColumna(
      @ForAll @StringLength(min = 250, max = 250) String description) {
    assertEquals(description, new Book("isbn", "nome", "serial", description).getDescription());
  }

  @Property(tries = 10)
  void descricionUnCaracterPorRibaDaColumna(
      @ForAll @StringLength(min = 251, max = 251) String description) {
    assertEquals(description, new Book("isbn", "nome", "serial", description).getDescription());
  }
}
