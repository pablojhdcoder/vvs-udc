package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;


class PublisherProperties {

  @Property(tries = 50)
  void constructorConservaElNombre(@ForAll @StringLength(max = 100) String name) {
    Publisher publisher = new Publisher(name);

    assertAll(
        "Comprobar que el constructor guarda cualquier nombre de hasta 100 caracteres",
        () -> assertEquals(name, publisher.getName())
    );
  }

  @Property(tries = 30)
  void constructorDejaBooksVacio(@ForAll @StringLength(max = 100) String name) {
    Publisher publisher = new Publisher(name);

    assertAll(
        "Comprobar que, sea cual sea el nombre, la editorial nace sin libros",
        () -> assertTrue(publisher.getBooks().isEmpty())
    );
  }

  @Property(tries = 50)
  void setNameConservaElValor(@ForAll @StringLength(max = 100) String name) {
    Publisher publisher = new Publisher();

    publisher.setName(name);

    assertAll(
        "Comprobar que setName guarda cualquier nombre de hasta 100 caracteres",
        () -> assertEquals(name, publisher.getName())
    );
  }

  @Property(tries = 50)
  void setIdConservaElValor(@ForAll long id) {
    Publisher publisher = new Publisher("Pearson");

    publisher.setId(id);

    assertAll(
        "Comprobar que setId guarda cualquier long (incluidos negativos, 0 y extremos)",
        () -> assertEquals(id, publisher.getId())
    );
  }

  @Property(tries = 30)
  void setBooksConNLibrosDistintosTieneTamanoN(@ForAll @IntRange(min = 0, max = 20) int n) {
    Publisher publisher = new Publisher("Pearson");
    Set<Book> books = new HashSet<>();
    for (int i = 0; i < n; i++) {
      books.add(new Book("isbn-" + i, "Book" + i, "SN-" + i, "desc"));
    }

    publisher.setBooks(books);

    assertAll(
        "Comprobar que con N libros distintos la editorial tiene exactamente N libros",
        () -> assertEquals(n, publisher.getBooks().size())
    );
  }
}