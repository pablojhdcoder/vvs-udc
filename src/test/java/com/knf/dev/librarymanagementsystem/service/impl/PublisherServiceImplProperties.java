package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.support.InMemoryPublisherCatalog;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;


class PublisherServiceImplProperties {

  @Property(tries = 30)
  void findAllDevuelveLoQueTieneElRepositorio(@ForAll @IntRange(min = 0, max = 30) int n) {
    PublisherRepository repository = mock(PublisherRepository.class);
    List<Publisher> publishers = editoriales(n);
    when(repository.findAll()).thenReturn(publishers);

    List<Publisher> result = new PublisherServiceImpl(repository).findAllPublishers();

    assertAll(
        "Comprobar que findAll devuelve las N editoriales del repositorio sin tocarlas",
        () -> assertSame(publishers, result)
    );
  }

  @Property(tries = 30)
  void findByIdExistenteDevuelveLaEditorial(@ForAll long id) {
    PublisherRepository repository = mock(PublisherRepository.class);
    Publisher publisher = new Publisher("Pearson");
    publisher.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(publisher));

    Publisher result = new PublisherServiceImpl(repository).findPublisherById(id);

    assertAll(
        "Comprobar que para cualquier ID existente se devuelve esa editorial",
        () -> assertSame(publisher, result)
    );
  }

  @Property(tries = 30)
  void findByIdInexistenteLanza(@ForAll long id) {
    PublisherRepository repository = mock(PublisherRepository.class);
    when(repository.findById(id)).thenReturn(Optional.empty());
    PublisherServiceImpl service = new PublisherServiceImpl(repository);

    assertAll(
        "Comprobar que para cualquier ID inexistente se lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> service.findPublisherById(id))
    );
  }

  @Property(tries = 30)
  void findByIdInexistenteMencionaElId(@ForAll long id) {
    PublisherRepository repository = mock(PublisherRepository.class);
    when(repository.findById(id)).thenReturn(Optional.empty());
    PublisherServiceImpl service = new PublisherServiceImpl(repository);

    assertAll(
        "Comprobar que el mensaje de error contiene el ID buscado",
        () -> assertEquals("Publisher not found  with ID " + id,
            assertThrows(NotFoundException.class, () -> service.findPublisherById(id)).getMessage())
    );
  }

  @Property(tries = 30)
  void createSiempreHaceSave(@ForAll @StringLength(max = 100) String name) {
    PublisherRepository repository = mock(PublisherRepository.class);
    Publisher publisher = new Publisher(name);

    new PublisherServiceImpl(repository).createPublisher(publisher);

    assertAll(
        "Comprobar que crear cualquier editorial llama a save con esa editorial",
        () -> verify(repository).save(publisher)
    );
  }

  @Property(tries = 30)
  void updateSiempreHaceSave(@ForAll @StringLength(max = 100) String name) {
    PublisherRepository repository = mock(PublisherRepository.class);
    Publisher publisher = new Publisher(name);

    new PublisherServiceImpl(repository).updatePublisher(publisher);

    assertAll(
        "Comprobar que actualizar cualquier editorial llama a save con esa editorial",
        () -> verify(repository).save(publisher)
    );
  }

  @Property(tries = 30)
  void deleteExistenteBorraPorSuId(@ForAll long id) {
    PublisherRepository repository = mock(PublisherRepository.class);
    Publisher publisher = new Publisher("Pearson");
    publisher.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(publisher));

    new PublisherServiceImpl(repository).deletePublisher(id);

    assertAll(
        "Comprobar que borrar una editorial existente llama a deleteById con su ID",
        () -> verify(repository).deleteById(id)
    );
  }

  @Property(tries = 30)
  void deleteInexistenteNuncaBorra(@ForAll long id) {
    PublisherRepository repository = mock(PublisherRepository.class);
    when(repository.findById(id)).thenReturn(Optional.empty());
    PublisherServiceImpl service = new PublisherServiceImpl(repository);

    assertAll(
        "Comprobar que si la editorial no existe se lanza NotFoundException y nunca se llama a deleteById",
        () -> assertThrows(NotFoundException.class, () -> service.deletePublisher(id)),
        () -> verify(repository, never()).deleteById(anyLong())
    );
  }

  @Property(tries = 20)
  void trasCrearFindByIdDevuelveElNombre(@ForAll @StringLength(max = 100) String name) {
    PublisherServiceImpl service = new InMemoryPublisherCatalog().service();
    Publisher publisher = new Publisher(name);

    service.createPublisher(publisher);

    assertAll(
        "Comprobar que tras crear una editorial se puede leer con el mismo nombre",
        () -> assertEquals(name, service.findPublisherById(publisher.getId()).getName())
    );
  }

  @Property(tries = 20)
  void trasCrearNEditorialesFindAllTieneN(@ForAll @IntRange(min = 0, max = 20) int n) {
    InMemoryPublisherCatalog catalogo = new InMemoryPublisherCatalog();
    for (int i = 0; i < n; i++) {
      catalogo.service().createPublisher(new Publisher("publisher-" + i));
    }

    assertAll(
        "Comprobar que tras crear N editoriales findAll devuelve N",
        () -> assertEquals(n, catalogo.service().findAllPublishers().size())
    );
  }

  @Property(tries = 20)
  void trasActualizarElNombreEsElNuevo(@ForAll @StringLength(max = 100) String name) {
    PublisherServiceImpl service = new InMemoryPublisherCatalog().service();
    Publisher publisher = new Publisher("Viejo");
    service.createPublisher(publisher);
    Publisher stored = service.findPublisherById(publisher.getId());
    stored.setName(name);

    service.updatePublisher(stored);

    assertAll(
        "Comprobar que tras actualizar el nombre se lee el nombre nuevo",
        () -> assertEquals(name, service.findPublisherById(publisher.getId()).getName())
    );
  }

  @Property(tries = 20)
  void trasActualizarNoHayEditorialesNuevas(@ForAll @StringLength(max = 100) String name) {
    InMemoryPublisherCatalog catalogo = new InMemoryPublisherCatalog();
    Publisher publisher = new Publisher("Viejo");
    catalogo.service().createPublisher(publisher);
    publisher.setName(name);

    catalogo.service().updatePublisher(publisher);

    assertAll(
        "Comprobar que actualizar no añade otra editorial al catálogo",
        () -> assertEquals(1, catalogo.size())
    );
  }

  @Property(tries = 20)
  void trasBorrarFindAllEstaVacio(@ForAll @StringLength(max = 100) String name) {
    PublisherServiceImpl service = new InMemoryPublisherCatalog().service();
    Publisher publisher = new Publisher(name);
    service.createPublisher(publisher);

    service.deletePublisher(publisher.getId());

    assertAll(
        "Comprobar que tras borrar la única editorial findAll queda vacío",
        () -> assertTrue(service.findAllPublishers().isEmpty())
    );
  }

  @Property(tries = 20)
  void trasBorrarFindByIdLanza(@ForAll @StringLength(max = 100) String name) {
    PublisherServiceImpl service = new InMemoryPublisherCatalog().service();
    Publisher publisher = new Publisher(name);
    service.createPublisher(publisher);
    Long id = publisher.getId();

    service.deletePublisher(id);

    assertAll(
        "Comprobar que tras borrar una editorial buscarla lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> service.findPublisherById(id))
    );
  }

  private List<Publisher> editoriales(int n) {
    List<Publisher> publishers = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      Publisher publisher = new Publisher("publisher-" + i);
      publisher.setId((long) i);
      publishers.add(publisher);
    }
    return publishers;
  }
}