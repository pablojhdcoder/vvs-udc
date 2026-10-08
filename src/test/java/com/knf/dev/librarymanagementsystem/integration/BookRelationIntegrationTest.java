package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relaciones de Book con Author, Category y Publisher guardadas en H2.
 *
 * <pre>
 *   [Vacio] --add--> [ConElementos]
 *   [ConElementos] --remove--> [Vacio]
 * </pre>
 *
 * <p>Tras cada cambio se vacía el contexto y se vuelve a leer la tabla de unión.
 */
@SpringBootTest
@Transactional
class BookRelationIntegrationTest {

  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private EntityManager entityManager;

  @BeforeEach
  void catalogoVacio() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
    publisherRepository.deleteAll();
  }

  private Book guardarLibro(String isbn) {
    return bookRepository.save(new Book(isbn, "Nombre", "SER-1", "descripcion"));
  }

  private void recargar() {
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("libro guardado: autores vacíos")
  void libroGuardadoNoTieneAutores() {
    Book book = guardarLibro("REL-AUT-0");
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getAuthors().isEmpty());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: el libro recargado conserva el autor")
  void anadirAutorSeConservaEnElLibro() {
    Author author = authorRepository.save(new Author("Martin", "descripcion"));
    Book book = guardarLibro("REL-AUT-1");
    book.addAuthors(author);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals("Martin", stored.getAuthors().iterator().next().getName());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: el autor recargado conserva el libro")
  void anadirAutorSeConservaEnElAutor() {
    Author author = authorRepository.save(new Author("Fowler", "descripcion"));
    Book book = guardarLibro("REL-AUT-2");
    book.addAuthors(author);
    bookRepository.save(book);
    recargar();

    Author stored = authorRepository.findById(author.getId()).orElseThrow();
    assertEquals("REL-AUT-2", stored.getBooks().iterator().next().getIsbn());
  }

  @Test
  @DisplayName("ConElementos --remove--> Vacio: el libro recargado no tiene autores")
  void quitarAutorDejaSinAutores() {
    Author author = authorRepository.save(new Author("Beck", "descripcion"));
    Book book = guardarLibro("REL-AUT-3");
    book.addAuthors(author);
    bookRepository.save(book);
    book.removeAuthors(author);
    bookRepository.save(book);
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getAuthors().isEmpty());
  }

  @Test
  @DisplayName("libro guardado: categorías vacías")
  void libroGuardadoNoTieneCategorias() {
    Book book = guardarLibro("REL-CAT-0");
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getCategories().isEmpty());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: el libro recargado conserva la categoría")
  void anadirCategoriaSeConservaEnElLibro() {
    Category category = categoryRepository.save(new Category("Novela"));
    Book book = guardarLibro("REL-CAT-1");
    book.addCategories(category);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals("Novela", stored.getCategories().iterator().next().getName());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: la categoría recargada conserva el libro")
  void anadirCategoriaSeConservaEnLaCategoria() {
    Category category = categoryRepository.save(new Category("Ensayo"));
    Book book = guardarLibro("REL-CAT-2");
    book.addCategories(category);
    bookRepository.save(book);
    recargar();

    Category stored = categoryRepository.findById(category.getId()).orElseThrow();
    assertEquals("REL-CAT-2", stored.getBooks().iterator().next().getIsbn());
  }

  @Test
  @DisplayName("ConElementos --remove--> Vacio: el libro recargado no tiene categorías")
  void quitarCategoriaDejaSinCategorias() {
    Category category = categoryRepository.save(new Category("Poesia"));
    Book book = guardarLibro("REL-CAT-3");
    book.addCategories(category);
    bookRepository.save(book);
    book.removeCategories(category);
    bookRepository.save(book);
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getCategories().isEmpty());
  }

  @Test
  @DisplayName("libro guardado: editoriales vacías")
  void libroGuardadoNoTieneEditoriales() {
    Book book = guardarLibro("REL-EDI-0");
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getPublishers().isEmpty());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: el libro recargado conserva la editorial")
  void anadirEditorialSeConservaEnElLibro() {
    Publisher publisher = publisherRepository.save(new Publisher("Planeta"));
    Book book = guardarLibro("REL-EDI-1");
    book.addPublishers(publisher);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals("Planeta", stored.getPublishers().iterator().next().getName());
  }

  @Test
  @DisplayName("Vacio --add--> ConElementos: la editorial recargada conserva el libro")
  void anadirEditorialSeConservaEnLaEditorial() {
    Publisher publisher = publisherRepository.save(new Publisher("Anagrama"));
    Book book = guardarLibro("REL-EDI-2");
    book.addPublishers(publisher);
    bookRepository.save(book);
    recargar();

    Publisher stored = publisherRepository.findById(publisher.getId()).orElseThrow();
    assertEquals("REL-EDI-2", stored.getBooks().iterator().next().getIsbn());
  }

  @Test
  @DisplayName("ConElementos --remove--> Vacio: el libro recargado no tiene editoriales")
  void quitarEditorialDejaSinEditoriales() {
    Publisher publisher = publisherRepository.save(new Publisher("Alianza"));
    Book book = guardarLibro("REL-EDI-3");
    book.addPublishers(publisher);
    bookRepository.save(book);
    book.removePublishers(publisher);
    bookRepository.save(book);
    recargar();

    assertTrue(bookRepository.findById(book.getId()).orElseThrow().getPublishers().isEmpty());
  }
}
