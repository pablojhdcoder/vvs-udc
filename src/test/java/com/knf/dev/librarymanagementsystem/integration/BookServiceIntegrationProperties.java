package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

/** Datos aleatorios de BookService contra H2. Un assert por propiedad. */
@JqwikSpringSupport
@SpringBootTest
class BookServiceIntegrationProperties {

  @Autowired private BookService bookService;
  @Autowired private BookRepository bookRepository;

  @Property(tries = 8)
  @Label("crear con isbn aleatorio y leerlo devuelve ese isbn")
  void crearLuegoLeerConservaIsbn(
      @ForAll @AlphaChars @StringLength(min = 1, max = 12) String sufijo) {
    bookRepository.deleteAll();
    String isbn = "R-" + sufijo;
    bookService.createBook(new Book(isbn, "Nombre", "SER-1", "descripcion"));
    Book stored = bookService.findAllBooks().get(0);

    assertEquals(isbn, bookService.findBookById(stored.getId()).getIsbn());
  }

  @Property(tries = 6)
  @Label("buscar por un nombre aleatorio encuentra ese libro")
  void buscarPorNombreLoEncuentra(
      @ForAll @AlphaChars @StringLength(min = 3, max = 10) String nombre) {
    bookRepository.deleteAll();
    bookService.createBook(new Book("S-" + nombre, nombre, "SER-1", "descripcion"));

    assertEquals(1, bookService.searchBooks(nombre).size());
  }

  @Property(tries = 6)
  @Label("la página no supera el tamaño pedido")
  void paginacionNoSuperaElTamano(
      @ForAll @IntRange(min = 0, max = 6) int total,
      @ForAll @IntRange(min = 1, max = 3) int size) {
    bookRepository.deleteAll();
    for (int i = 0; i < total; i++) {
      bookService.createBook(
          new Book("P" + total + size + i, "Libro " + i, "S" + i, "d"));
    }

    assertTrue(bookService.findPaginated(PageRequest.of(0, size)).getContent().size() <= size);
  }

  @Property(tries = 6)
  @Label("el total de la página es el número de libros guardados")
  void paginacionConservaElTotal(
      @ForAll @IntRange(min = 0, max = 6) int total,
      @ForAll @IntRange(min = 1, max = 3) int size) {
    bookRepository.deleteAll();
    for (int i = 0; i < total; i++) {
      bookService.createBook(
          new Book("T" + total + size + i, "Libro " + i, "S" + i, "d"));
    }

    assertEquals(
        total, bookService.findPaginated(PageRequest.of(0, size)).getTotalElements());
  }

  @Property(tries = 5)
  @Label("un id que no está en H2 lanza NotFoundException")
  void idInexistenteLanza(@ForAll @LongRange(min = 100_000, max = 200_000) long id) {
    bookRepository.deleteAll();

    assertThrows(NotFoundException.class, () -> bookService.findBookById(id));
  }
}
