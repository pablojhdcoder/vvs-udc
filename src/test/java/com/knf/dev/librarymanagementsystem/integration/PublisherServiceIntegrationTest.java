package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

@SpringBootTest
@Transactional
class PublisherServiceIntegrationTest {

  @Autowired
  private PublisherService publisherService;

  @Autowired
  private PublisherRepository publisherRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private EntityManager entityManager;

  @BeforeEach
  void catalogoVacio() {
    bookRepository.deleteAll();
    publisherRepository.deleteAll();
  }

  private void recargar() {
    entityManager.flush();
    entityManager.clear();
  }

  private Long crear(String name) {
    Publisher publisher = new Publisher(name);
    publisherService.createPublisher(publisher);
    recargar();
    return publisher.getId();
  }

  @Test
  void testFindAllPublishersWhenEmpty() {
    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que sin editoriales en la base findAll devuelve una lista vacía",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testCreatePublisherAssignsId() {
    Long id = crear("Pearson");

    assertAll(
        "Comprobar que al crear una editorial la base le asigna un ID",
        () -> assertNotNull(id)
    );
  }

  @Test
  void testCreatePublisherThenFindById() {
    Long id = crear("Pearson");

    assertAll(
        "Comprobar que una editorial creada se lee de la base con el mismo nombre",
        () -> assertEquals("Pearson", publisherService.findPublisherById(id).getName())
    );
  }

  @Test
  void testCreatePublisherAppearsInFindAll() {
    crear("Pearson");

    assertAll(
        "Comprobar que tras crear una editorial findAll devuelve exactamente una",
        () -> assertEquals(1, publisherService.findAllPublishers().size())
    );
  }

  @Test
  void testCreateTwoPublishers() {
    crear("Pearson");
    crear("Anaya");

    assertAll(
        "Comprobar que tras crear dos editoriales findAll devuelve dos",
        () -> assertEquals(2, publisherService.findAllPublishers().size())
    );
  }

  @Test
  void testUpdatePublisherChangesName() {
    Long id = crear("Pearson");
    Publisher stored = publisherService.findPublisherById(id);
    stored.setName("Pearson Educación");

    publisherService.updatePublisher(stored);
    recargar();

    assertAll(
        "Comprobar que el nombre actualizado queda guardado en la base",
        () -> assertEquals("Pearson Educación", publisherService.findPublisherById(id).getName())
    );
  }

  @Test
  void testUpdatePublisherDoesNotCreateAnother() {
    Long id = crear("Pearson");
    Publisher stored = publisherService.findPublisherById(id);
    stored.setName("Anaya");

    publisherService.updatePublisher(stored);
    recargar();

    assertAll(
        "Comprobar que actualizar una editorial no crea una fila nueva",
        () -> assertEquals(1, publisherService.findAllPublishers().size())
    );
  }

  @Test
  void testDeletePublisher() {
    Long id = crear("Pearson");

    publisherService.deletePublisher(id);
    recargar();

    assertAll(
        "Comprobar que tras borrar una editorial ya no se encuentra",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(id))
    );
  }

  @Test
  void testDeletePublisherAmongSeveral() {
    Long idBorrada = crear("Pearson");
    Long idQueQueda = crear("Anaya");

    publisherService.deletePublisher(idBorrada);
    recargar();

    assertAll(
        "Comprobar que al borrar una editorial entre varias solo desaparece esa",
        () -> assertTrue(publisherRepository.findById(idBorrada).isEmpty()),
        () -> assertEquals("Anaya", publisherService.findPublisherById(idQueQueda).getName())
    );
  }

  @Test
  void testFindPublisherByIdWhenDoesNotExist() {
    assertAll(
        "Comprobar que buscar un ID que no está en la base lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(99L))
    );
  }

  @Test
  void testFindPublisherByIdZero() {
    assertAll(
        "Comprobar que el ID 0 (frontera) no existe y lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(0L))
    );
  }

  @Test
  void testDeletePublisherWhenDoesNotExist() {
    assertAll(
        "Comprobar que borrar un ID que no está en la base lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(99L))
    );
  }

  @Test
  void testEmptyNamePersists() {
    Long id = crear("");

    assertAll(
        "Comprobar que un nombre vacío (frontera inferior) se guarda en la columna",
        () -> assertEquals("", publisherService.findPublisherById(id).getName())
    );
  }

  @Test
  void testName100Persists() {
    String name = "a".repeat(100);
    Long id = crear(name);

    assertAll(
        "Comprobar que un nombre de 100 caracteres (frontera superior) se guarda en la columna",
        () -> assertEquals(name, publisherService.findPublisherById(id).getName())
    );
  }

  @Test
  void testName101DoesNotFit() {
    assertAll(
        "Comprobar que un nombre de 101 caracteres no cabe en la columna",
        () -> assertThrows(PersistenceException.class, () -> {
          publisherService.createPublisher(new Publisher("a".repeat(101)));
          entityManager.flush();
        })
    );
  }

  @Test
  void testDuplicateNameDoesNotFit() {
    crear("Pearson");

    assertAll(
        "Comprobar que no se pueden guardar dos editoriales con el mismo nombre",
        () -> assertThrows(PersistenceException.class, () -> {
          publisherService.createPublisher(new Publisher("Pearson"));
          entityManager.flush();
        })
    );
  }

  @Test
  void testPublisherKeepsItsBooks() {
    Book book = new Book("ISBN-PUB-1", "Libro", "SER-1", "descripcion");
    Publisher publisher = new Publisher("Con libros");
    book.addPublishers(publisher);
    bookRepository.save(book);
    recargar();

    assertAll(
        "Comprobar que al leer una editorial desde el servicio se cargan sus libros",
        () -> assertEquals(1, publisherService.findPublisherById(publisher.getId()).getBooks().size())
    );
  }

  @Test
  void testPublisherWithBooksCannotBeDeleted() {
    Book book = new Book("ISBN-PUB-2", "Libro", "SER-2", "descripcion");
    Publisher publisher = new Publisher("Con libros");
    book.addPublishers(publisher);
    bookRepository.save(book);
    recargar();
    Long id = publisher.getId();

    assertAll(
        "Comprobar que una editorial que tiene libros asociados no se puede borrar",
        () -> assertThrows(PersistenceException.class, () -> {
          publisherService.deletePublisher(id);
          entityManager.flush();
        })
    );
  }

  @Test
  void testPublisherLifecycle() {
    boolean vaciaAlPrincipio = publisherService.findAllPublishers().isEmpty();
    Long id = crear("Pearson");
    String nombreLeido = publisherService.findPublisherById(id).getName();
    Publisher stored = publisherService.findPublisherById(id);
    stored.setName("Anaya");
    publisherService.updatePublisher(stored);
    recargar();
    String nombreActualizado = publisherService.findPublisherById(id).getName();
    publisherService.deletePublisher(id);
    recargar();

    assertAll(
        "Comprobar el ciclo vacío → crear → leer → actualizar → borrar → no encontrado contra H2",
        () -> assertTrue(vaciaAlPrincipio),
        () -> assertEquals("Pearson", nombreLeido),
        () -> assertEquals("Anaya", nombreActualizado),
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(id))
    );
  }
}