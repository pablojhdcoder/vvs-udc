package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.spring.JqwikSpringSupport;

@JqwikSpringSupport
@SpringBootTest
class PublisherServiceIntegrationProperties {

  @Autowired
  private PublisherService publisherService;

  @Autowired
  private PublisherRepository publisherRepository;

  @Autowired
  private BookRepository bookRepository;

  private void vaciar() {
    bookRepository.deleteAll();
    publisherRepository.deleteAll();
  }

  @Property(tries = 6)
  void crearLuegoLeerConservaElNombre(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String name) {
    vaciar();
    Publisher publisher = new Publisher(name);
    publisherService.createPublisher(publisher);

    assertAll(
        "Comprobar que una editorial con nombre aleatorio se lee de H2 con ese nombre",
        () -> assertEquals(name, publisherService.findPublisherById(publisher.getId()).getName())
    );
  }

  @Property(tries = 6)
  void actualizarConservaElNombre(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String name) {
    vaciar();
    Publisher publisher = new Publisher("Fijo");
    publisherService.createPublisher(publisher);
    Publisher stored = publisherService.findPublisherById(publisher.getId());
    stored.setName(name);
    publisherService.updatePublisher(stored);

    assertAll(
        "Comprobar que al actualizar con un nombre aleatorio H2 guarda ese nombre",
        () -> assertEquals(name, publisherService.findPublisherById(publisher.getId()).getName())
    );
  }

  @Property(tries = 5)
  void crearNEditorialesFindAllDevuelveN(@ForAll @IntRange(min = 0, max = 6) int total) {
    vaciar();
    for (int i = 0; i < total; i++) {
      publisherService.createPublisher(new Publisher("N" + total + "-" + i));
    }

    assertAll(
        "Comprobar que con N editoriales guardadas findAll devuelve N",
        () -> assertEquals(total, publisherService.findAllPublishers().size())
    );
  }

  @Property(tries = 5)
  void borrarDejaElCatalogoVacio(@ForAll @AlphaChars @StringLength(min = 1, max = 100) String name) {
    vaciar();
    Publisher publisher = new Publisher(name);
    publisherService.createPublisher(publisher);
    publisherService.deletePublisher(publisher.getId());

    assertAll(
        "Comprobar que tras crear y borrar una editorial no queda ninguna en H2",
        () -> assertTrue(publisherService.findAllPublishers().isEmpty())
    );
  }

  @Property(tries = 4)
  void idInexistenteLanza(@ForAll @LongRange(min = 100_000, max = 200_000) long id) {
    vaciar();

    assertAll(
        "Comprobar que un ID que no está en H2 lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(id))
    );
  }
}