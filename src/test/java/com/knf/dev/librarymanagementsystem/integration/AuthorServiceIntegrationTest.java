package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integración AuthorService + AuthorRepository (H2).
 * flush y clear obligan a leer la fila de la tabla, no la copia que sigue en memoria.
 *
 * <pre>
 *   [Ausente] --createAuthor--> [Presente]
 *   [Presente] --updateAuthor--> [Presente]
 *   [Presente] --deleteAuthor--> [Ausente]
 *   [Ausente] --findAuthorById--> NotFoundException
 *   [Ausente] --deleteAuthor--> NotFoundException
 * </pre>
 */
@SpringBootTest
@Transactional
class AuthorServiceIntegrationTest {

  @Autowired private AuthorService authorService;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private BookRepository bookRepository;
  @Autowired private EntityManager entityManager;

  @BeforeEach
  void catalogoVacio() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  private Author crear(String name, String description) {
    authorService.createAuthor(new Author(name, description));
    entityManager.flush();
    entityManager.clear();
    return authorService.findAllAuthors().get(0);
  }

  @Test
  @DisplayName("Ausente: findAll está vacío")
  void ausenteFindAllVacio() {
    assertTrue(authorService.findAllAuthors().isEmpty());
  }

  @Test
  @DisplayName("Ausente --create--> Presente: findById devuelve el nombre")
  void createLuegoSeLeeElNombre() {
    Author guardado = crear("Ada", "primera");

    assertEquals("Ada", authorService.findAuthorById(guardado.getId()).getName());
  }

  @Test
  @DisplayName("Ausente --create--> Presente: la base asigna un id")
  void createAsignaId() {
    Author guardado = crear("Alan", "computacion");

    assertNotNull(guardado.getId());
  }

  @Test
  @DisplayName("Ausente --create--> Presente: findAll tiene un autor")
  void createApareceEnFindAll() {
    crear("Grace", "cobol");

    assertEquals(1, authorService.findAllAuthors().size());
  }

  @Test
  @DisplayName("dos creates dejan dos autores")
  void dosCreates() {
    crear("Ada", "una");
    authorService.createAuthor(new Author("Alan", "otra"));
    entityManager.flush();
    entityManager.clear();

    assertEquals(2, authorService.findAllAuthors().size());
  }

  @Test
  @DisplayName("Presente --update--> Presente: el nombre nuevo persiste")
  void updateCambiaElNombre() {
    Author guardado = crear("Ada", "primera");
    guardado.setName("Ada Lovelace");

    authorService.updateAuthor(guardado);
    entityManager.flush();
    entityManager.clear();

    assertEquals("Ada Lovelace", authorService.findAuthorById(guardado.getId()).getName());
  }

  @Test
  @DisplayName("Presente --update--> Presente: la descripción nueva persiste")
  void updateCambiaLaDescripcion() {
    Author guardado = crear("Ada", "primera");
    guardado.setDescription("matematica");

    authorService.updateAuthor(guardado);
    entityManager.flush();
    entityManager.clear();

    assertEquals("matematica", authorService.findAuthorById(guardado.getId()).getDescription());
  }

  @Test
  @DisplayName("Presente --delete--> Ausente")
  void deleteDespuesNoEncuentra() {
    Author guardado = crear("Ada", "primera");
    Long id = guardado.getId();

    authorService.deleteAuthor(id);
    entityManager.flush();
    entityManager.clear();

    assertThrows(NotFoundException.class, () -> authorService.findAuthorById(id));
  }

  @Test
  @DisplayName("Ausente --find--> NotFoundException")
  void findEnAusente() {
    assertThrows(NotFoundException.class, () -> authorService.findAuthorById(99L));
  }

  @Test
  @DisplayName("frontera id 0 inexistente")
  void idCero() {
    assertThrows(NotFoundException.class, () -> authorService.findAuthorById(0L));
  }

  @Test
  @DisplayName("Ausente --delete--> NotFoundException")
  void deleteEnAusente() {
    assertThrows(NotFoundException.class, () -> authorService.deleteAuthor(99L));
  }

  @Test
  @DisplayName("frontera: nombre vacío cabe en la columna")
  void nombreVacioPersiste() {
    Author guardado = crear("", "desc");

    assertEquals("", authorService.findAuthorById(guardado.getId()).getName());
  }

  @Test
  @DisplayName("frontera: nombre de longitud 100 persiste")
  void nombreDe100Persiste() {
    String name = "a".repeat(100);
    Author guardado = crear(name, "desc");

    assertEquals(name, authorService.findAuthorById(guardado.getId()).getName());
  }

  @Test
  @DisplayName("frontera: nombre de longitud 101 no cabe")
  void nombreDe101NoCabe() {
    assertThrows(
        PersistenceException.class,
        () -> {
          authorService.createAuthor(new Author("a".repeat(101), "desc"));
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("frontera: descripción de longitud 250 persiste")
  void descripcionDe250Persiste() {
    String description = "d".repeat(250);
    Author guardado = crear("DescLarga", description);

    assertEquals(description, authorService.findAuthorById(guardado.getId()).getDescription());
  }

  @Test
  @DisplayName("frontera: descripción de longitud 251 no cabe")
  void descripcionDe251NoCabe() {
    assertThrows(
        PersistenceException.class,
        () -> {
          authorService.createAuthor(new Author("DescFuera", "d".repeat(251)));
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("frontera: un segundo autor con el mismo nombre no cabe")
  void nombreDuplicadoNoCabe() {
    crear("Ada", "una");

    assertThrows(
        PersistenceException.class,
        () -> {
          authorService.createAuthor(new Author("Ada", "otra"));
          entityManager.flush();
        });
  }

  @Test
  @DisplayName("página 0 tamaño 2 con 5 autores devuelve 2")
  void primeraPagina() {
    for (int i = 0; i < 5; i++) {
      authorService.createAuthor(new Author("Pag-" + i, "d"));
    }
    entityManager.flush();
    entityManager.clear();

    assertEquals(2, authorService.findPaginated(PageRequest.of(0, 2)).getContent().size());
  }

  @Test
  @DisplayName("con 5 autores y tamaño 2 el total sigue siendo 5")
  void totalDeLaPaginacion() {
    for (int i = 0; i < 5; i++) {
      authorService.createAuthor(new Author("Tot-" + i, "d"));
    }
    entityManager.flush();
    entityManager.clear();

    assertEquals(5, authorService.findPaginated(PageRequest.of(0, 2)).getTotalElements());
  }

  @Test
  @DisplayName("última página parcial: un autor")
  void ultimaPaginaParcial() {
    for (int i = 0; i < 5; i++) {
      authorService.createAuthor(new Author("Par-" + i, "d"));
    }
    entityManager.flush();
    entityManager.clear();

    Page<Author> pagina = authorService.findPaginated(PageRequest.of(2, 2));

    assertEquals(1, pagina.getContent().size());
  }

  @Test
  @DisplayName("frontera: la página justo después del último elemento queda vacía")
  void paginaEnLaFrontera() {
    crear("Uno", "d");
    authorService.createAuthor(new Author("Dos", "d"));
    entityManager.flush();
    entityManager.clear();

    assertTrue(authorService.findPaginated(PageRequest.of(1, 2)).getContent().isEmpty());
  }

  @Test
  @DisplayName("catálogo vacío: contenido vacío")
  void paginacionVaciaContenido() {
    assertTrue(authorService.findPaginated(PageRequest.of(0, 5)).getContent().isEmpty());
  }

  @Test
  @DisplayName("catálogo vacío: total 0")
  void paginacionVaciaTotal() {
    assertEquals(0, authorService.findPaginated(PageRequest.of(0, 5)).getTotalElements());
  }

  @Test
  @DisplayName("pageSize mayor que la lista devuelve todos")
  void pageSizeMayorQueLaLista() {
    crear("Uno", "d");
    authorService.createAuthor(new Author("Dos", "d"));
    entityManager.flush();
    entityManager.clear();

    assertEquals(2, authorService.findPaginated(PageRequest.of(0, 10)).getContent().size());
  }
}
