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
 * Diagrama de estados da relación (authors; categories e publishers son o mesmo grafo).
 *
 * <pre>
 *   [Vacio] --add(novo)--> [ConElementos]
 *   [ConElementos] --add(o mesmo)--> [ConElementos]
 *   [ConElementos] --add(outro)--> [ConElementos]
 *   [ConElementos] --remove(o último)--> [Vacio]
 *   [ConElementos] --remove(un de varios)--> [ConElementos]
 *   [Vacio] --remove--> [Vacio]
 *   [Vacio] --set(non vacío)--> [ConElementos]
 *   [ConElementos] --set(vacío)--> [Vacio]
 * </pre>
 *
 * <p>Cada transición ten un escenario. Un assert por proba.
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
  @DisplayName("Constructor baleiro")
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
    @DisplayName("authors empeza baleiro")
    void authorsBaleiro() {
      assertTrue(new Book().getAuthors().isEmpty());
    }

    @Test
    @DisplayName("categories empeza baleiro")
    void categoriesBaleiro() {
      assertTrue(new Book().getCategories().isEmpty());
    }

    @Test
    @DisplayName("publishers empeza baleiro")
    void publishersBaleiro() {
      assertTrue(new Book().getPublishers().isEmpty());
    }
  }

  @Nested
  @DisplayName("Constructor con argumentos")
  class ConstructorConArgumentos {

    @Test
    @DisplayName("garda o isbn típico")
    void gardaIsbn() {
      assertEquals(ISBN, libro().getIsbn());
    }

    @Test
    @DisplayName("garda o nome")
    void gardaNome() {
      assertEquals(NAME, libro().getName());
    }

    @Test
    @DisplayName("garda o serialName")
    void gardaSerial() {
      assertEquals(SERIAL, libro().getSerialName());
    }

    @Test
    @DisplayName("garda a descrición")
    void gardaDescricion() {
      assertEquals(DESCRIPTION, libro().getDescription());
    }

    @Test
    @DisplayName("o id segue null ata que o asigne a persistencia")
    void idSegueNull() {
      assertNull(libro().getId());
    }

    @Test
    @DisplayName("authors segue baleiro")
    void authorsBaleiro() {
      assertTrue(libro().getAuthors().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("isbn: valor curto, baleiro, lonxitude 50 e 51")
    void isbnEnFronteira(String isbn) {
      Book book = new Book(isbn, NAME, SERIAL, DESCRIPTION);

      assertEquals(isbn, book.getIsbn());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraNome")
    @DisplayName("name: valor curto, baleiro, lonxitude 100 e 101")
    void nomeEnFronteira(String name) {
      Book book = new Book(ISBN, name, SERIAL, DESCRIPTION);

      assertEquals(name, book.getName());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("serialName: valor curto, baleiro, lonxitude 50 e 51")
    void serialEnFronteira(String serial) {
      Book book = new Book(ISBN, NAME, serial, DESCRIPTION);

      assertEquals(serial, book.getSerialName());
    }

    @ParameterizedTest
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraDescricion")
    @DisplayName("description: valor curto, baleiro, lonxitude 250 e 251")
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
    @DisplayName("setId conserva 1, 0 e o máximo long")
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
    @DisplayName("setIsbn: null, curto, baleiro, 50 e 51")
    void setIsbn(String isbn) {
      Book book = libro();
      book.setIsbn(isbn);

      assertEquals(isbn, book.getIsbn());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraNome")
    @DisplayName("setName: null, curto, baleiro, 100 e 101")
    void setName(String name) {
      Book book = libro();
      book.setName(name);

      assertEquals(name, book.getName());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraIsbnSerial")
    @DisplayName("setSerialName: null, curto, baleiro, 50 e 51")
    void setSerialName(String serial) {
      Book book = libro();
      book.setSerialName(serial);

      assertEquals(serial, book.getSerialName());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("com.knf.dev.librarymanagementsystem.entity.BookTest#textosFronteiraDescricion")
    @DisplayName("setDescription: null, curto, baleiro, 250 e 251")
    void setDescription(String description) {
      Book book = libro();
      book.setDescription(description);

      assertEquals(description, book.getDescription());
    }

    @Test
    @DisplayName("setAuthors substitúe a colección")
    void setAuthorsSubstitue() {
      Book book = libro();
      Set<Author> autores = new HashSet<>();
      autores.add(new Author("Martin", "Uncle Bob"));

      book.setAuthors(autores);

      assertSame(autores, book.getAuthors());
    }

    @Test
    @DisplayName("setCategories substitúe a colección")
    void setCategoriesSubstitue() {
      Book book = libro();
      Set<Category> categorias = new HashSet<>();
      categorias.add(new Category("Software"));

      book.setCategories(categorias);

      assertSame(categorias, book.getCategories());
    }

    @Test
    @DisplayName("setPublishers substitúe a colección")
    void setPublishersSubstitue() {
      Book book = libro();
      Set<Publisher> editoriais = new HashSet<>();
      editoriais.add(new Publisher("Prentice Hall"));

      book.setPublishers(editoriais);

      assertSame(editoriais, book.getPublishers());
    }
  }

  @Nested
  @DisplayName("Autores: operacións e transicións")
  class Autores {

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado do libro")
    void addPonElementoNoLibro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.addAuthors(author);

      assertTrue(book.getAuthors().contains(author));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado do autor")
    void addPonElementoNoAutor() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.addAuthors(author);

      assertTrue(author.getBooks().contains(book));
    }

    @Test
    @DisplayName("add do mesmo autor non duplica no libro")
    void addRepetidoNonDuplicaNoLibro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.addAuthors(author);

      assertEquals(1, book.getAuthors().size());
    }

    @Test
    @DisplayName("add do mesmo autor non duplica no autor")
    void addRepetidoNonDuplicaNoAutor() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.addAuthors(author);

      assertEquals(1, author.getBooks().size());
    }

    @Test
    @DisplayName("add doutro autor deixa dous no libro")
    void addDoutroAutor() {
      Book book = libro();
      book.addAuthors(new Author("Martin", "Uncle Bob"));

      book.addAuthors(new Author("Fowler", "Refactoring"));

      assertEquals(2, book.getAuthors().size());
    }

    @Test
    @DisplayName("ConElementos --remove(último)--> Vacio no libro")
    void removeUltimoDeixaLibroBaleiro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.removeAuthors(author);

      assertTrue(book.getAuthors().isEmpty());
    }

    @Test
    @DisplayName("ConElementos --remove(último)--> Vacio no autor")
    void removeUltimoDeixaAutorBaleiro() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");
      book.addAuthors(author);

      book.removeAuthors(author);

      assertTrue(author.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove dun de varios conserva o outro no libro")
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
    @DisplayName("remove dun de varios quita o libro dese autor")
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
    @DisplayName("Vacio --remove--> Vacio no libro")
    void removeEnVacioDeixaLibroBaleiro() {
      Book book = libro();

      book.removeAuthors(new Author("Martin", "Uncle Bob"));

      assertTrue(book.getAuthors().isEmpty());
    }

    @Test
    @DisplayName("Vacio --set(non vacío)--> ConElementos")
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
  @DisplayName("Categorías: operacións e transicións")
  class Categorias {

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado do libro")
    void addNoLibro() {
      Book book = libro();
      Category category = new Category("Software");

      book.addCategories(category);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado da categoría")
    void addNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");

      book.addCategories(category);

      assertTrue(category.getBooks().contains(book));
    }

    @Test
    @DisplayName("add da mesma categoría non duplica")
    void addRepetido() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.addCategories(category);

      assertEquals(1, book.getCategories().size());
    }

    @Test
    @DisplayName("add doutra categoría deixa dúas")
    void addOutra() {
      Book book = libro();
      book.addCategories(new Category("Software"));

      book.addCategories(new Category("Java"));

      assertEquals(2, book.getCategories().size());
    }

    @Test
    @DisplayName("add da mesma categoría non duplica no lado da categoría")
    void addRepetidoNonDuplicaNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.addCategories(category);

      assertEquals(1, category.getBooks().size());
    }

    @Test
    @DisplayName("remove dunha de varias conserva a outra no libro")
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
    @DisplayName("remove dunha de varias quita o libro desa categoría")
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
    @DisplayName("remove da última deixa o libro baleiro")
    void removeUltimaNoLibro() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.removeCategories(category);

      assertTrue(book.getCategories().isEmpty());
    }

    @Test
    @DisplayName("remove da última deixa a categoría sen ese libro")
    void removeUltimaNaCategoria() {
      Book book = libro();
      Category category = new Category("Software");
      book.addCategories(category);

      book.removeCategories(category);

      assertTrue(category.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove en baleiro segue baleiro")
    void removeEnVacio() {
      Book book = libro();

      book.removeCategories(new Category("Software"));

      assertTrue(book.getCategories().isEmpty());
    }

    @Test
    @DisplayName("setCategories non baleiro enche")
    void setEnche() {
      Book book = libro();
      Category category = new Category("Software");
      Set<Category> categorias = new HashSet<>();
      categorias.add(category);

      book.setCategories(categorias);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("setCategories baleiro volta a Vacio")
    void setBaleiro() {
      Book book = libro();
      book.addCategories(new Category("Software"));

      book.setCategories(new HashSet<>());

      assertTrue(book.getCategories().isEmpty());
    }
  }

  @Nested
  @DisplayName("Editoriais: operacións e transicións")
  class Editoriais {

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado do libro")
    void addNoLibro() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.addPublishers(publisher);

      assertTrue(book.getPublishers().contains(publisher));
    }

    @Test
    @DisplayName("Vacio --add--> ConElementos no lado da editorial")
    void addNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.addPublishers(publisher);

      assertTrue(publisher.getBooks().contains(book));
    }

    @Test
    @DisplayName("add da mesma editorial non duplica")
    void addRepetido() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.addPublishers(publisher);

      assertEquals(1, book.getPublishers().size());
    }

    @Test
    @DisplayName("add doutra editorial deixa dúas")
    void addOutra() {
      Book book = libro();
      book.addPublishers(new Publisher("Prentice Hall"));

      book.addPublishers(new Publisher("OReilly"));

      assertEquals(2, book.getPublishers().size());
    }

    @Test
    @DisplayName("add da mesma editorial non duplica no lado da editorial")
    void addRepetidoNonDuplicaNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.addPublishers(publisher);

      assertEquals(1, publisher.getBooks().size());
    }

    @Test
    @DisplayName("remove dunha de varias conserva a outra no libro")
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
    @DisplayName("remove dunha de varias quita o libro desa editorial")
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
    @DisplayName("remove da última deixa o libro baleiro")
    void removeUltimaNoLibro() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.removePublishers(publisher);

      assertTrue(book.getPublishers().isEmpty());
    }

    @Test
    @DisplayName("remove da última deixa a editorial sen ese libro")
    void removeUltimaNaEditorial() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      book.addPublishers(publisher);

      book.removePublishers(publisher);

      assertTrue(publisher.getBooks().isEmpty());
    }

    @Test
    @DisplayName("remove en baleiro segue baleiro")
    void removeEnVacio() {
      Book book = libro();

      book.removePublishers(new Publisher("Prentice Hall"));

      assertTrue(book.getPublishers().isEmpty());
    }

    @Test
    @DisplayName("setPublishers non baleiro enche")
    void setEnche() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");
      Set<Publisher> editoriais = new HashSet<>();
      editoriais.add(publisher);

      book.setPublishers(editoriais);

      assertTrue(book.getPublishers().contains(publisher));
    }

    @Test
    @DisplayName("setPublishers baleiro volta a Vacio")
    void setBaleiro() {
      Book book = libro();
      book.addPublishers(new Publisher("Prentice Hall"));

      book.setPublishers(new HashSet<>());

      assertTrue(book.getPublishers().isEmpty());
    }
  }

  @Nested
  @DisplayName("SpotBugs EI_EXPOSE_REP: o getter devolve a colección interna")
  class ExposeRep {

    @Test
    @DisplayName("getAuthors: mutar o set devolto cambia o libro")
    void getAuthors() {
      Book book = libro();
      Author author = new Author("Martin", "Uncle Bob");

      book.getAuthors().add(author);

      assertTrue(book.getAuthors().contains(author));
    }

    @Test
    @DisplayName("getCategories: mutar o set devolto cambia o libro")
    void getCategories() {
      Book book = libro();
      Category category = new Category("Software");

      book.getCategories().add(category);

      assertTrue(book.getCategories().contains(category));
    }

    @Test
    @DisplayName("getPublishers: mutar o set devolto cambia o libro")
    void getPublishers() {
      Book book = libro();
      Publisher publisher = new Publisher("Prentice Hall");

      book.getPublishers().add(publisher);

      assertTrue(book.getPublishers().contains(publisher));
    }
  }

  @Nested
  @DisplayName("SpotBugs EI_EXPOSE_REP2: o setter garda o set que lle pasan")
  class ExposeRep2 {

    @Test
    @DisplayName("setAuthors: mutar o set orixinal cambia o libro")
    void setAuthors() {
      Book book = libro();
      Set<Author> autores = new HashSet<>();
      book.setAuthors(autores);

      autores.add(new Author("Martin", "Uncle Bob"));

      assertEquals(1, book.getAuthors().size());
    }

    @Test
    @DisplayName("setCategories: mutar o set orixinal cambia o libro")
    void setCategories() {
      Book book = libro();
      Set<Category> categorias = new HashSet<>();
      book.setCategories(categorias);

      categorias.add(new Category("Software"));

      assertEquals(1, book.getCategories().size());
    }

    @Test
    @DisplayName("setPublishers: mutar o set orixinal cambia o libro")
    void setPublishers() {
      Book book = libro();
      Set<Publisher> editoriais = new HashSet<>();
      book.setPublishers(editoriais);

      editoriais.add(new Publisher("Prentice Hall"));

      assertEquals(1, book.getPublishers().size());
    }
  }
}
