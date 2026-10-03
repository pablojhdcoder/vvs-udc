package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Diagrama de estados de la relación (authors; categories y publishers son el mismo grafo).
 *
 * <pre>
 *   [Vacio] --add(nuevo)--> [ConElementos]
 *   [ConElementos] --add(el mismo)--> [ConElementos]
 *   [ConElementos] --add(otro)--> [ConElementos]
 *   [ConElementos] --remove(el último)--> [Vacio]
 *   [ConElementos] --remove(uno de varios)--> [ConElementos]
 *   [Vacio] --remove--> [Vacio]
 *   [Vacio] --set(no vacío)--> [ConElementos]
 *   [ConElementos] --set(vacío)--> [Vacio]
 * </pre>
 *
 * <p>Cada transición tiene un escenario. Un assert por prueba.
 */
class BookTest {

  private static final String ISBN = "978-0132350884";
  private static final String NAME = "Clean Code";
  private static final String SERIAL = "CC-1";
  private static final String DESCRIPTION = "Guia de codigo limpio";

  private Book libro() {
    return new Book(ISBN, NAME, SERIAL, DESCRIPTION);
  }

  static Stream<String> textosFronteiraIsbnSerial() {
    return Stream.of("a", "", "a".repeat(50), "a".repeat(51));
  }

  static Stream<String> textosFronteiraNome() {
    return Stream.of("a", "", "a".repeat(100), "a".repeat(101));
  }

  static Stream<String> textosFronteiraDescricion() {
    return Stream.of("a", "", "a".repeat(250), "a".repeat(251));
  }

  @Nested
  @DisplayName("Constructor vacío")
  class ConstructorBaleiro {

    @Test
    @DisplayName("id queda null")
    void idNull() {
      assertNull(new Book().getId());
    }

    @Test
    @DisplayName("isbn queda null")
    void isbnNull() {
      assertNull(new Book().getIsbn());
    }

    @Test
    @DisplayName("name queda null")
    void nameNull() {
      assertNull(new Book().getName());
    }

    @Test
    @DisplayName("serialName queda null")
    void serialNull() {
      assertNull(new Book().getSerialName());
    }

    @Test
    @DisplayName("description queda null")
    void descriptionNull() {
      assertNull(new Book().getDescription());
    }

    @Test
    @DisplayName("authors empieza vacío")
    void authorsBaleiro() {
      assertTrue(new Book().getAuthors().isEmpty());
    }

    @Test
    @DisplayName("categories empieza vacío")
    void categoriesBaleiro() {
      assertTrue(new Book().getCategories().isEmpty());
    }

    @Test
    @DisplayName("publishers empieza vacío")
    void publishersBaleiro() {
      assertTrue(new Book().getPublishers().isEmpty());
    }
  }

  @Nested
  @DisplayName("Constructor con argumentos")
  class ConstructorConArgumentos {

    @Test
    @DisplayName("guarda el isbn típico")
    void gardaIsbn() {
      assertEquals(ISBN, libro().getIsbn());
    }

    @Test
    @DisplayName("guarda el nombre")
    void gardaNome() {
      assertEquals(NAME, libro().getName());
    }

    @Test
    @DisplayName("guarda el serialName")
    void gardaSerial() {
      assertEquals(SERIAL, libro().getSerialName());
    }

    @Test
    @DisplayName("guarda la descripción")
    void gardaDescricion() {
      assertEquals(DESCRIPTION, libro().getDescription());
    }

    @Test
    @DisplayName("el id sigue null hasta que lo asigne la persistencia")
    void idSegueNull() {
      assertNull(libro().getId());
    }

    @Test
    @DisplayName("authors sigue vacío")
    void authorsBaleiro() {
      assertTrue(libro().getAuthors().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("isbn: valor corto, vacío, longitud 50 y 51")
    void isbnEnFronteira(String isbn) {
      Book book = new Book(isbn, NAME, SERIAL, DESCRIPTION);

      assertEquals(isbn, book.getIsbn());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraNome")
    @DisplayName("name: valor corto, vacío, longitud 100 y 101")
    void nomeEnFronteira(String name) {
      Book book = new Book(ISBN, name, SERIAL, DESCRIPTION);

      assertEquals(name, book.getName());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("serialName: valor corto, vacío, longitud 50 y 51")
    void serialEnFronteira(String serial) {
      Book book = new Book(ISBN, NAME, serial, DESCRIPTION);

      assertEquals(serial, book.getSerialName());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraDescricion")
    @DisplayName("description: valor corto, vacío, longitud 250 y 251")
    void descricionEnFronteira(String description) {
      Book book = new Book(ISBN, NAME, SERIAL, description);

      assertEquals(description, book.getDescription());
    }
  }

  @Nested
  @DisplayName("Setters")
  class Setters {

    @ParameterizedTest
    @ValueSource(longs = {1L, 0L, Long.MAX_VALUE})
    @DisplayName("setId conserva 1, 0 y el máximo long")
    void setId(long id) {
      Book book = new Book();
      book.setId(id);

      assertEquals(id, book.getId());
    }

    @Test
    @DisplayName("setId acepta null")
    void setIdNull() {
      Book book = libro();
      book.setId(7L);
      book.setId(null);

      assertNull(book.getId());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("setIsbn: null, corto, vacío, 50 y 51")
    void setIsbn(String isbn) {
      Book book = libro();
      book.setIsbn(isbn);

      assertEquals(isbn, book.getIsbn());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraNome")
    @DisplayName("setName: null, corto, vacío, 100 y 101")
    void setName(String name) {
      Book book = libro();
      book.setName(name);

      assertEquals(name, book.getName());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("setSerialName: null, corto, vacío, 50 y 51")
    void setSerialName(String serial) {
      Book book = libro();
      book.setSerialName(serial);

      assertEquals(serial, book.getSerialName());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraDescricion")
    @DisplayName("setDescription: null, corto, vacío, 250 y 251")
    void setDescription(String description) {
      Book book = libro();
      book.setDescription(description);

      assertEquals(description, book.getDescription());
    }

    @Test
    @DisplayName("setAuthors sustituye la colección")
    void setAuthorsSubstitue() {
      Book book = libro();
      Set<Author> autores = new HashSet<>();
      autores.add(new Author("Martin", "Uncle Bob"));

      book.setAuthors(autores);

      assertSame(autores, book.getAuthors());
    }

    @Test
    @DisplayName("setCategories sustituye la colección")
    void setCategoriesSubstitue() {
      Book book = libro();
      Set<Category> categorias = new HashSet<>();
      categorias.add(new Category("Software"));

      book.setCategories(categorias);

      assertSame(categorias, book.getCategories());
    }

    @Test
    @DisplayName("setPublishers sustituye la colección")
    void setPublishersSubstitue() {
      Book book = libro();
      Set<Publisher> editoriais = new HashSet<>();
      editoriais.add(new Publisher("Prentice Hall"));

      book.setPublishers(editoriais);

      assertSame(editoriais, book.getPublishers());
    }
  }

  @Nested
  @DisplayName("Autores: operaciones y transiciones")
  class Autores {

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado del libro")
    void addPonElementoNoLibro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.addAuthors(author);

      assertTrue(book.getAuthors().contains(author));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado del autor")
    void addPonElementoNoAutor() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.addAuthors(author);

      assertTrue(author.getBooks().contains(book));
    }

    @Test
    @DisplayName("add del mismo autor no duplica en el libro")
    void addRepetidoNonDuplicaNoLibro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.addAuthors(author);

      assertEquals(1, book.getAuthors().size());
    }

    @Test
    @DisplayName("add del mismo autor no duplica en el autor")
    void addRepetidoNonDuplicaNoAutor() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.addAuthors(author);

      assertEquals(1, author.getBooks().size());
    }

    @Test
    @DisplayName("add de otro autor deja dos en el libro")
    void addDoutroAutor() {
      Book book = libro();
      book.addAuthors(new Author("Martin", "Uncle Bob"));

      book.addAuthors(new Author("Fowler", "Refactoring"));

      assertEquals(2, book.getAuthors().size());
    }

    @Test
    @DisplayName("ConElementos --remove(último)--> Vacio en el libro")
    void removeUltimoDeixaLibroBaleiro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.removeAuthors(author);

      assertTrue(book.getAuthors().isEmpty());
    }

    @Test
    @DisplayName("ConElementos --remove(último)--> Vacio en el autor")
    void removeUltimoDeixaAutorBaleiro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.removeAuthors(author);

      assertTrue(author.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove de uno de varios conserva el otro en el libro")
    void removeUnDeVariosConservaOutro() {
      Book book = libro();
      Author martin = new Author("Martin", "Uncle Bob");
      Author fowler = new Author("Fowler", "Refactoring");
      book.addAuthors(martin);
      book.addAuthors(fowler);

      book.removeAuthors(martin);

      assertTrue(book.getAuthors().contains(fowler));
    }

    @Test
    @DisplayName("remove de uno de varios quita el libro de ese autor")
    void removeUnDeVariosLimpaEseAutor() {
      Book book = libro();
      Author martin = new Author("Martin", "Uncle Bob");
      Author fowler = new Author("Fowler", "Refactoring");
      book.addAuthors(martin);
      book.addAuthors(fowler);

      book.removeAuthors(martin);

      assertFalse(martin.getBooks().contains(book));
    }

    @Test
    @DisplayName("Vacio --remove--> Vacio en el libro")
    void removeEnVacioDeixaLibroBaleiro() {
      Book book = libro();

      book.removeAuthors(new Author("Martin", "Uncle Bob"));

      assertTrue(book.getAuthors().isEmpty());
    }

    @Test
    @DisplayName("Vacio --set(no vacío)--> ConElementos")
    void setAuthorsEnche() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      Set<Author> autores = new HashSet<>();
      autores.add(author);

      book.setAuthors(autores);

      assertTrue(book.getAuthors().contains(author));
    }

    @Test
    @DisplayName("ConElementos --set(vacío)--> Vacio")
    void setAuthorsBaleiro() {
      Book book = libro();
      book.addAuthors(new Author("Martin", "Uncle Bob"));

      book.setAuthors(new HashSet<>());

      assertTrue(book.getAuthors().isEmpty());
    }
  }

  @Nested
  @DisplayName("Categorías: operaciones y transiciones")
  class Categorias {

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado del libro")
    void addNoLibro() {
      Book book = libro();
      Category category = new Category("Software");

      book.addCategories(category);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado de la categoría")
    void addNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");

      book.addCategories(category);

      assertTrue(category.getBooks().contains(book));
    }

    @Test
    @DisplayName("add de la misma categoría no duplica")
    void addRepetido() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.addCategories(category);

      assertEquals(1, book.getCategories().size());
    }

    @Test
    @DisplayName("add de otra categoría deja dos")
    void addOutra() {
      Book book = libro();
      book.addCategories(new Category("Software"));

      book.addCategories(new Category("Java"));

      assertEquals(2, book.getCategories().size());
    }

    @Test
    @DisplayName("add de la misma categoría no duplica en el lado de la categoría")
    void addRepetidoNonDuplicaNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.addCategories(category);

      assertEquals(1, category.getBooks().size());
    }

    @Test
    @DisplayName("remove de una de varias conserva la otra en el libro")
    void removeUnhaDeVariasConservaOutra() {
      Book book = libro();
      Category software = new Category("Software");
      Category java = new Category("Java");
      book.addCategories(software);
      book.addCategories(java);

      book.removeCategories(software);

      assertTrue(book.getCategories().contains(java));
    }

    @Test
    @DisplayName("remove de una de varias quita el libro de esa categoría")
    void removeUnhaDeVariasLimpaEsaCategoria() {
      Book book = libro();
      Category software = new Category("Software");
      Category java = new Category("Java");
      book.addCategories(software);
      book.addCategories(java);

      book.removeCategories(software);

      assertFalse(software.getBooks().contains(book));
    }

    @Test
    @DisplayName("remove de la última deja el libro vacío")
    void removeUltimaNoLibro() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.removeCategories(category);

      assertTrue(book.getCategories().isEmpty());
    }

    @Test
    @DisplayName("remove de la última deja la categoría sin ese libro")
    void removeUltimaNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.removeCategories(category);

      assertTrue(category.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove en vacío sigue vacío")
    void removeEnVacio() {
      Book book = libro();

      book.removeCategories(new Category("Software"));

      assertTrue(book.getCategories().isEmpty());
    }

    @Test
    @DisplayName("setCategories no vacío llena")
    void setEnche() {
      Book book = libro();
      Category category = new Category("Software");
      Set<Category> categorias = new HashSet<>();
      categorias.add(category);

      book.setCategories(categorias);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("setCategories vacío vuelve a Vacio")
    void setBaleiro() {
      Book book = libro();
      book.addCategories(new Category("Software"));

      book.setCategories(new HashSet<>());

      assertTrue(book.getCategories().isEmpty());
    }
  }

  @Nested
  @DisplayName("Editoriales: operaciones y transiciones")
  class Editoriais {

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado del libro")
    void addNoLibro() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.addPublishers(publisher);

      assertTrue(book.getPublishers().contains(publisher));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos en el lado de la editorial")
    void addNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.addPublishers(publisher);

      assertTrue(publisher.getBooks().contains(book));
    }

    @Test
    @DisplayName("add de la misma editorial no duplica")
    void addRepetido() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.addPublishers(publisher);

      assertEquals(1, book.getPublishers().size());
    }

    @Test
    @DisplayName("add de otra editorial deja dos")
    void addOutra() {
      Book book = libro();
      book.addPublishers(new Publisher("Prentice Hall"));

      book.addPublishers(new Publisher("OReilly"));

      assertEquals(2, book.getPublishers().size());
    }

    @Test
    @DisplayName("add de la misma editorial no duplica en el lado de la editorial")
    void addRepetidoNonDuplicaNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.addPublishers(publisher);

      assertEquals(1, publisher.getBooks().size());
    }

    @Test
    @DisplayName("remove de una de varias conserva la otra en el libro")
    void removeUnhaDeVariasConservaOutra() {
      Book book = libro();
      Publisher prentice = new Publisher("Prentice Hall");
      Publisher oreilly = new Publisher("OReilly");
      book.addPublishers(prentice);
      book.addPublishers(oreilly);

      book.removePublishers(prentice);

      assertTrue(book.getPublishers().contains(oreilly));
    }

    @Test
    @DisplayName("remove de una de varias quita el libro de esa editorial")
    void removeUnhaDeVariasLimpaEsaEditorial() {
      Book book = libro();
      Publisher prentice = new Publisher("Prentice Hall");
      Publisher oreilly = new Publisher("OReilly");
      book.addPublishers(prentice);
      book.addPublishers(oreilly);

      book.removePublishers(prentice);

      assertFalse(prentice.getBooks().contains(book));
    }

    @Test
    @DisplayName("remove de la última deja el libro vacío")
    void removeUltimaNoLibro() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.removePublishers(publisher);

      assertTrue(book.getPublishers().isEmpty());
    }

    @Test
    @DisplayName("remove de la última deja la editorial sin ese libro")
    void removeUltimaNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.removePublishers(publisher);

      assertTrue(publisher.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove en vacío sigue vacío")
    void removeEnVacio() {
      Book book = libro();

      book.removePublishers(new Publisher("Prentice Hall"));

      assertTrue(book.getPublishers().isEmpty());
    }

    @Test
    @DisplayName("setPublishers no vacío llena")
    void setEnche() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      Set<Publisher> editoriais = new HashSet<>();
      editoriais.add(publisher);

      book.setPublishers(editoriais);

      assertTrue(book.getPublishers().contains(publisher));
    }

    @Test
    @DisplayName("setPublishers vacío vuelve a Vacio")
    void setBaleiro() {
      Book book = libro();
      book.addPublishers(new Publisher("Prentice Hall"));

      book.setPublishers(new HashSet<>());

      assertTrue(book.getPublishers().isEmpty());
    }
  }

  @Nested
  @DisplayName("SpotBugs EI_EXPOSE_REP: el getter devuelve la colección interna")
  class ExposeRep {

    @Test
    @DisplayName("getAuthors: mutar el set devuelto cambia el libro")
    void getAuthors() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.getAuthors().add(author);

      assertTrue(book.getAuthors().contains(author));
    }

    @Test
    @DisplayName("getCategories: mutar el set devuelto cambia el libro")
    void getCategories() {
      Book book = libro();
      Category category = new Category("Software");

      book.getCategories().add(category);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("getPublishers: mutar el set devuelto cambia el libro")
    void getPublishers() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.getPublishers().add(publisher);

      assertTrue(book.getPublishers().contains(publisher));
    }
  }

  @Nested
  @DisplayName("SpotBugs EI_EXPOSE_REP2: el setter guarda el set que le pasan")
  class ExposeRep2 {

    @Test
    @DisplayName("setAuthors: mutar el set original cambia el libro")
    void setAuthors() {
      Book book = libro();
      Set<Author> autores = new HashSet<>();
      book.setAuthors(autores);

      autores.add(new Author("Martin", "Uncle Bob"));

      assertEquals(1, book.getAuthors().size());
    }

    @Test
    @DisplayName("setCategories: mutar el set original cambia el libro")
    void setCategories() {
      Book book = libro();
      Set<Category> categorias = new HashSet<>();
      book.setCategories(categorias);

      categorias.add(new Category("Software"));

      assertEquals(1, book.getCategories().size());
    }

    @Test
    @DisplayName("setPublishers: mutar el set original cambia el libro")
    void setPublishers() {
      Book book = libro();
      Set<Publisher> editoriais = new HashSet<>();
      book.setPublishers(editoriais);

      editoriais.add(new Publisher("Prentice Hall"));

      assertEquals(1, book.getPublishers().size());
    }
  }
}
