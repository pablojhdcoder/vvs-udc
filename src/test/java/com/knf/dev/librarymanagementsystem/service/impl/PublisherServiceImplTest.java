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
  void testFindAllPublishersWithNoPublishers() {
    List<Publisher> emptyPublishers = new ArrayList<>();
    when(publisherRepository.findAll()).thenReturn(emptyPublishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtiene una lista vacía cuando no existen editoriales",
        () -> assertTrue(result.isEmpty()),
        () -> verify(publisherRepository).findAll()
    );
  }

  @Test
  void testFindAllPublishersWithOnePublisher() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtiene correctamente una editorial",
        () -> assertEquals(publishers, result),
        () -> verify(publisherRepository).findAll()
    );
  }

  @Test
  void testFindAllPublishersWithTwoPublishers() {
    List<Publisher> publishers = List.of(publisher(1L, "Pearson"), publisher(2L, "Anaya"));
    when(publisherRepository.findAll()).thenReturn(publishers);

    List<Publisher> result = publisherService.findAllPublishers();

    assertAll(
        "Comprobar que se obtienen correctamente dos editoriales y en el mismo orden",
        () -> assertEquals(publishers, result),
        () -> verify(publisherRepository).findAll()
    );
  }


  @Test
  void testFindPublisherById() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    Publisher result = publisherService.findPublisherById(1L);

    assertAll(
        "Comprobar que se encuentra correctamente la editorial con ID 1",
        () -> assertEquals(publisher, result),
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
  void testDeletePublisher() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar que se busca y elimina correctamente una editorial existente",
        () -> verify(publisherRepository).findById(1L),
        () -> verify(publisherRepository).deleteById(1L)
    );
  }

  @Test
  void testDeletePublisherWhenPublisherDoesNotExist() {
    when(publisherRepository.findById(2L)).thenReturn(Optional.empty());

    assertAll(
        "Comprobar que se lanza NotFoundException al eliminar una editorial inexistente y no se borra nada",
        () -> assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(2L)),
        () -> verify(publisherRepository, never()).deleteById(anyLong())
    );
  }


  @Test
  void testPublisherLifecycle() {
    Publisher publisher = publisher(1L, "Pearson");
    when(publisherRepository.findAll()).thenReturn(List.of(), List.of(publisher));
    when(publisherRepository.findById(1L))
        .thenReturn(Optional.of(publisher), Optional.of(publisher), Optional.empty());

    List<Publisher> alPrincipio = publisherService.findAllPublishers();
    publisherService.createPublisher(publisher);
    List<Publisher> trasCrear = publisherService.findAllPublishers();
    String nombreLeido = publisherService.findPublisherById(1L).getName();
    publisher.setName("Anaya");
    publisherService.updatePublisher(publisher);
    publisherService.deletePublisher(1L);

    assertAll(
        "Comprobar el ciclo vacío → crear → leer → actualizar → borrar → no encontrado",
        () -> assertTrue(alPrincipio.isEmpty()),
        () -> assertEquals(List.of(publisher), trasCrear),
        () -> assertEquals("Pearson", nombreLeido),
        () -> verify(publisherRepository, times(2)).save(publisher),
        () -> verify(publisherRepository).deleteById(1L),
        () -> assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(1L))
    );
  }
}