package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;


@ExtendWith(MockitoExtension.class)
class PublisherServiceImplTest {

  @Mock
  PublisherRepository publisherRepository;

  @InjectMocks
  PublisherServiceImpl publisherService;

  private Publisher publisher(long id, String name) {
    Publisher publisher = new Publisher(name);
    publisher.setId(id);
    return publisher;
  }


  @Test
  void testFindAllPublishersWithNoPublishers_isEmpty() {
    List<Publisher> emptyPublishers = new ArrayList<>();
    when(publisherRepository.findAll()).thenReturn(emptyPublishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtiene una lista vacía cuando no existen editoriales",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testFindAllPublishersWithNoPublishers_callsFindAll() {
    List<Publisher> emptyPublishers = new ArrayList<>();
    when(publisherRepository.findAll()).thenReturn(emptyPublishers);

    publisherService.findAllPublishers();

    assertAll(
        "Comprobar que findAllPublishers sin editoriales consulta el repositorio",
        () -> verify(publisherRepository).findAll()
    );
  }

  @Test
  void testFindAllPublishersWithOnePublisher_equals() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtiene correctamente una editorial",
        () -> assertEquals(publishers, result)
    );
  }

  @Test
  void testFindAllPublishersWithOnePublisher_callsFindAll() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    publisherService.findAllPublishers();

    assertAll(
        "Comprobar que findAllPublishers con una editorial consulta el repositorio",
        () -> verify(publisherRepository).findAll()
    );
  }

  @Test
  void testFindAllPublishersWithTwoPublishers_equals() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"), publisher(2L, "Anaya"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtienen correctamente dos editoriales y en el mismo orden",
        () -> assertEquals(publishers, result)
    );
  }

  @Test
  void testFindAllPublishersWithTwoPublishers_callsFindAll() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"), publisher(2L, "Anaya"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    publisherService.findAllPublishers();

    assertAll(
        "Comprobar que findAllPublishers con dos editoriales consulta el repositorio",
        () -> verify(publisherRepository).findAll()
    );
  }


  @Test
  void testFindPublisherById_equals() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    Publisher result = publisherService.findPublisherById(1L);

    assertAll(
        "Comprobar que se encuentra correctamente la editorial con ID 1",
        () -> assertEquals(publisher, result)
    );
  }

  @Test
  void testFindPublisherById_callsFindById() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.findPublisherById(1L);

    assertAll(
        "Comprobar que findPublisherById consulta el repositorio con el ID",
        () -> verify(publisherRepository).findById(1L)
    );
  }

  @Test
  void testFindPublisherByIdWhenPublisherDoesNotExist() {
    when(publisherRepository.findById(3L)).thenReturn(Optional.empty());

    assertAll(
        "Comprobar que se lanza NotFoundException cuando no existe la editorial con ID 3",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(3L))
    );
  }

  @Test
  void testFindPublisherByIdNotFoundMessage() {
    when(publisherRepository.findById(3L)).thenReturn(Optional.empty());

    assertAll(
        "Comprobar que el mensaje de NotFoundException incluye el ID buscado",
        () -> assertEquals("Publisher not found  with ID 3",
            assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(3L))
                .getMessage())
    );
  }


  @Test
  void testCreatePublisher() {
    Publisher publisher = new Publisher("Pearson");

    publisherService.createPublisher(publisher);

    assertAll(
        "Comprobar que se guarda correctamente una editorial",
        () -> verify(publisherRepository).save(publisher)
    );
  }

  @Test
  void testCreatePublisherWithNull() {
    publisherService.createPublisher(null);

    assertAll(
        "Comprobar que el servicio no valida y pasa el null tal cual al repositorio",
        () -> verify(publisherRepository).save(null)
    );
  }


  @Test
  void testUpdatePublisher() {
    Publisher publisher = publisher(1L, "Pearson");
    publisher.setName("Anaya");

    publisherService.updatePublisher(publisher);

    assertAll(
        "Comprobar que se guarda correctamente la editorial con ID 1 al actualizarla",
        () -> verify(publisherRepository).save(publisher)
    );
  }

  @Test
  void testUpdatePublisherWithNull() {
    publisherService.updatePublisher(null);

    assertAll(
        "Comprobar que actualizar con null también se delega sin validar en el repositorio",
        () -> verify(publisherRepository).save(null)
    );
  }

  // ---------- deletePublisher ----------

  @Test
  void testDeletePublisher_findsById() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar que al eliminar se busca la editorial por ID",
        () -> verify(publisherRepository).findById(1L)
    );
  }

  @Test
  void testDeletePublisher_deletesById() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar que al eliminar se llama a deleteById con el ID",
        () -> verify(publisherRepository).deleteById(1L)
    );
  }

  @Test
  void testDeletePublisherWhenPublisherDoesNotExist_throws() {
    when(publisherRepository.findById(2L)).thenReturn(Optional.empty());

    assertAll(
        "Comprobar que se lanza NotFoundException al eliminar una editorial inexistente",
        () -> assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(2L))
    );
  }

  @Test
  void testDeletePublisherWhenPublisherDoesNotExist_neverDeletes() {
    when(publisherRepository.findById(2L)).thenReturn(Optional.empty());

    try {
      publisherService.deletePublisher(2L);
    } catch (NotFoundException ignored) {
      // esperado: la editorial no existe
    }

    assertAll(
        "Comprobar que al eliminar una editorial inexistente no se llama a deleteById",
        () -> verify(publisherRepository, never()).deleteById(anyLong())
    );
  }


  @Test
  void testPublisherLifecycle_emptyAtStart() {
    when(publisherRepository.findAll()).thenReturn(List.of());

    List<Publisher> alPrincipio = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que al inicio del ciclo el catálogo está vacío",
        () -> assertTrue(alPrincipio.isEmpty())
    );
  }

  @Test
  void testPublisherLifecycle_afterCreate() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findAll()).thenReturn(List.of(publisher));

    publisherService.createPublisher(publisher);
    List<Publisher> trasCrear = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que tras crear en el ciclo findAll devuelve esa editorial",
        () -> assertEquals(List.of(publisher), trasCrear)
    );
  }

  @Test
  void testPublisherLifecycle_readName() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.createPublisher(publisher);
    String nombreLeido = publisherService.findPublisherById(1L).getName();

    assertAll(
        "Comprobar que tras crear en el ciclo se lee el nombre original",
        () -> assertEquals("Pearson", nombreLeido)
    );
  }

  @Test
  void testPublisherLifecycle_savesOnCreateAndUpdate() {
    Publisher publisher = publisher(1L, "Pearson");

    publisherService.createPublisher(publisher);
    publisher.setName("Anaya");
    publisherService.updatePublisher(publisher);

    assertAll(
        "Comprobar que el ciclo crear → actualizar hace save dos veces",
        () -> verify(publisherRepository, times(2)).save(publisher)
    );
  }

  @Test
  void testPublisherLifecycle_deletesById() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.createPublisher(publisher);
    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar que el ciclo borra la editorial por ID",
        () -> verify(publisherRepository).deleteById(1L)
    );
  }

  @Test
  void testPublisherLifecycle_notFoundAfterDelete() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L))
        .thenReturn(Optional.of(publisher), Optional.empty());

    publisherService.createPublisher(publisher);
    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar que tras borrar en el ciclo buscar la editorial lanza NotFoundException",
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(1L))
    );
  }
}