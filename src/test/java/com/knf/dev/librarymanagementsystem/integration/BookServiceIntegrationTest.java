package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integración BookService + BookRepository (H2). Os mocks da proba de unidade desaparecen.
 *
 * <pre>
 *   [Ausente] --createBook--> [Presente]
 *   [Presente] --updateBook--> [Presente]
 *   [Presente] --deleteBook--> [Ausente]
 *   [Ausente] --findBookById--> [Ausente] + NotFoundException
 *   [Ausente] --deleteBook--> [Ausente] + NotFoundException
 * </pre>
 */
@SpringBootTest
@Transactional
class BookServiceIntegrationTest {

  @Autowired private BookService bookService;

  @Autowired private BookRepository bookRepository;

  @BeforeEach
  void catalogoBaleiro() {
    bookRepository.deleteAll();
  }

  private Book libro(String isbn, String name) {
    return new Book(isbn, name, "SER-1", "descricion");
  }

  private Book crearDespoisLer(Book book) {
    bookService.createBook(book);
    return bookService.findAllBooks().get(0);
  }

  @Test
  @DisplayName("Ausente --create--> Presente: findById devolve o isbn")
  void createDespoisAtopase() {
    Book gardado = crearDespoisLer(libro("ISBN-1", "Clean Code"));

    assertEquals("ISBN-1", bookService.findBookById(gardado.getId()).getIsbn());
  }

  @Test
  @DisplayName("Ausente --create--> Presente: findAll ten un libro")
  void createApareceEnFindAll() {
    bookService.createBook(libro("ISBN-2", "Refactoring"));

    assertEquals(1, bookService.findAllBooks().size());
  }

  @Test
  @DisplayName("dous creates dejan dous libros")
  void dousCreates() {
    bookService.createBook(libro("ISBN-3A", "Un"));
    bookService.createBook(libro("ISBN-3B", "Dous"));

    assertEquals(2, bookService.findAllBooks().size());
  }

  @Test
  @DisplayName("Presente --update--> Presente: o nome novo persiste")
  void updateCambiaNome() {
    Book gardado = crearDespoisLer(libro("ISBN-4", "Vello"));
    gardado.setName("Novo");

    bookService.updateBook(gardado);

    assertEquals("Novo", bookService.findBookById(gardado.getId()).getName());
  }

  @Test
  @DisplayName("Presente --update--> Presente: o isbn novo persiste")
  void updateCambiaIsbn() {
    Book gardado = crearDespoisLer(libro("ISBN-5", "Clean Code"));
    gardado.setIsbn("ISBN-5B");

    bookService.updateBook(gardado);

    assertEquals("ISBN-5B", bookService.findBookById(gardado.getId()).getIsbn());
  }

  @Test
  @DisplayName("Presente --delete--> Ausente")
  void deleteDespoisNonAtopa() {
    Book gardado = crearDespoisLer(libro("ISBN-6", "Clean Code"));
    Long id = gardado.getId();

    bookService.deleteBook(id);

    assertThrows(NotFoundException.class, () -> bookService.findBookById(id));
  }

  @Test
  @DisplayName("Ausente --find--> NotFoundException")
  void findEnAusente() {
    assertThrows(NotFoundException.class, () -> bookService.findBookById(99L));
  }

  @Test
  @DisplayName("Ausente --delete--> NotFoundException")
  void deleteEnAusente() {
    assertThrows(NotFoundException.class, () -> bookService.deleteBook(99L));
  }

  @Test
  @DisplayName("search null co catálogo baleiro devolve lista baleira")
  void searchNullBaleiro() {
    assertTrue(bookService.searchBooks(null).isEmpty());
  }

  @Test
  @DisplayName("search null devolve os libros creados")
  void searchNullDevolveTodos() {
    bookService.createBook(libro("ISBN-7", "Clean Code"));

    assertEquals(1, bookService.searchBooks(null).size());
  }

  @Test
  @DisplayName("search por nome atopa o libro")
  void searchPorNome() {
    bookService.createBook(libro("ISBN-8", "Clean Code"));
    bookService.createBook(libro("ISBN-9", "Outro"));

    assertEquals(1, bookService.searchBooks("Clean").size());
  }

  @Test
  @DisplayName("search por isbn atopa o libro")
  void searchPorIsbn() {
    bookService.createBook(libro("ISBN-BUSCA", "Clean Code"));

    assertEquals("ISBN-BUSCA", bookService.searchBooks("BUSCA").get(0).getIsbn());
  }

  @Test
  @DisplayName("search sen coincidencias devolve baleiro")
  void searchSenCoincidencias() {
    bookService.createBook(libro("ISBN-10", "Clean Code"));

    assertTrue(bookService.searchBooks("zzzz-non-existe").isEmpty());
  }

  @Test
  @DisplayName("páxina 0 tamaño 2 con 5 libros devolve 2")
  void primeiraPaxina() {
    for (int i = 0; i < 5; i++) {
      bookService.createBook(libro("PAG-" + i, "Libro " + i));
    }

    assertEquals(2, bookService.findPaginated(PageRequest.of(0, 2)).getContent().size());
  }

  @Test
  @DisplayName("fronteira: páxina xusto despois do último elemento queda baleira")
  void paxinaNaFronteira() {
    bookService.createBook(libro("FR-1", "Un"));
    bookService.createBook(libro("FR-2", "Dous"));

    assertTrue(bookService.findPaginated(PageRequest.of(1, 2)).getContent().isEmpty());
  }

  @Test
  @DisplayName("con 5 libros e tamaño 2 o total segue sendo 5")
  void totalDaPaxinacion() {
    for (int i = 0; i < 5; i++) {
      bookService.createBook(libro("TOT-" + i, "Libro " + i));
    }

    assertEquals(5, bookService.findPaginated(PageRequest.of(0, 2)).getTotalElements());
  }
}
