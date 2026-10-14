package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
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

/**
 * Datos aleatorios de AuthorService contra H2. Un assert por propiedad. 
 */
@JqwikSpringSupport
@SpringBootTest
class AuthorServiceIntegrationProperties {

  @Autowired private AuthorService authorService;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private BookRepository bookRepository;

  private void vaciar() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  @Property(tries = 6)
  @Label("crear con nombre aleatorio y leerlo devuelve ese nombre")
  void crearLuegoLeerConservaElNombre(
      @ForAll @AlphaChars @StringLength(min = 1, max = 12) String name) {
    vaciar();
    authorService.createAuthor(new Author(name, "desc"));
    Author stored = authorService.findAllAuthors().get(0);

    assertEquals(name, authorService.findAuthorById(stored.getId()).getName());
  }

  @Property(tries = 6)
  @Label("actualizar con una descripción aleatoria persiste ese texto")
  void actualizarConservaLaDescripcion(
      @ForAll @AlphaChars @StringLength(min = 1, max = 20) String description) {
    vaciar();
    authorService.createAuthor(new Author("Fijo", "vieja"));
    Author stored = authorService.findAllAuthors().get(0);
    stored.setDescription(description);
    authorService.updateAuthor(stored);

    assertEquals(description, authorService.findAuthorById(stored.getId()).getDescription());
  }

  @Property(tries = 5)
  @Label("la página no supera el tamaño pedido")
  void paginacionNoSuperaElTamano(
      @ForAll @IntRange(min = 0, max = 6) int total, @ForAll @IntRange(min = 1, max = 3) int size) {
    vaciar();
    for (int i = 0; i < total; i++) {
      authorService.createAuthor(new Author("P" + total + "s" + size + "i" + i, "d"));
    }

    assertTrue(authorService.findPaginated(PageRequest.of(0, size)).getContent().size() <= size);
  }

  @Property(tries = 5)
  @Label("el total de la página es el número de autores guardados")
  void paginacionConservaElTotal(
      @ForAll @IntRange(min = 0, max = 6) int total, @ForAll @IntRange(min = 1, max = 3) int size) {
    vaciar();
    for (int i = 0; i < total; i++) {
      authorService.createAuthor(new Author("T" + total + "s" + size + "i" + i, "d"));
    }

    assertEquals(total, authorService.findPaginated(PageRequest.of(0, size)).getTotalElements());
  }

  @Property(tries = 4)
  @Label("un id que no está en H2 lanza NotFoundException")
  void idInexistenteLanza(@ForAll @LongRange(min = 100_000, max = 200_000) long id) {
    vaciar();

    assertThrows(NotFoundException.class, () -> authorService.findAuthorById(id));
  }
}
