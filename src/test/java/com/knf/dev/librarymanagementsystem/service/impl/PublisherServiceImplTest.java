package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;

class PublisherServiceImplTest {

    private PublisherRepository publisherRepository;
	private PublisherServiceImpl publisherService;

    @BeforeEach
	void setUp() {
		publisherRepository = mock(PublisherRepository.class);
		publisherService = new PublisherServiceImpl(publisherRepository);
	}

    private Publisher editorial(long id, String nombre) {
		Publisher publisher = new Publisher(nombre);
		publisher.setId(id);
		return publisher;
    }
    
    @Nested
	@DisplayName("Lectura")

	class Lectura {

		@Test
		@DisplayName("findAllPublishers delega en repository.findAll")
		void findAllPublishersDevuelveLoDelRepositorio() {
			List<Publisher> editoriales = List.of(editorial(1L, "Prentice Hall"), editorial(2L, "Pearson"));
			when(publisherRepository.findAll()).thenReturn(editoriales);

			List<Publisher> resultado = publisherService.findAllPublishers();

			assertEquals(editoriales, resultado);
			verify(publisherRepository).findAll();
		}

		@Test
		@DisplayName("findAllPublishers devuelve lista vacía si no hay editoriales")
		void findAllPublishersVacio() {
			when(publisherRepository.findAll()).thenReturn(List.of());

			assertTrue(publisherService.findAllPublishers().isEmpty());
		}
	}

	@Nested
	@DisplayName("findById y delete: existe / no encontrado")
	class PorId {

		@Test
		@DisplayName("findPublisherById devuelve la editorial si el repositorio la tiene")
		void findPublisherByIdExistente() {
			Publisher publisher = editorial(1L, "Prentice Hall");
			when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

			assertEquals(publisher, publisherService.findPublisherById(1L));
		}

		@Test
		@DisplayName("findPublisherById lanza NotFoundException si el Optional está vacío")
		void findPublisherByIdInexistente() {
			when(publisherRepository.findById(99L)).thenReturn(Optional.empty());

			NotFoundException ex = assertThrows(NotFoundException.class,
					() -> publisherService.findPublisherById(99L));
			assertEquals("Publisher not found  with ID 99", ex.getMessage());
		}

		@Test
		@DisplayName("deletePublisher busca y borra por el id de la editorial encontrada")
		void deletePublisherExistente() {
			Publisher publisher = editorial(1L, "Prentice Hall");
			when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

			publisherService.deletePublisher(1L);

			verify(publisherRepository).findById(1L);
			verify(publisherRepository).deleteById(1L);
		}

		@Test
		@DisplayName("deletePublisher lanza NotFoundException y no llama a deleteById")
		void deletePublisherInexistente() {
			when(publisherRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> publisherService.deletePublisher(99L));
			verify(publisherRepository, never()).deleteById(anyLong());
		}
	}

	@Nested
	@DisplayName("Escritura")
	class Escritura {

		@Test
		@DisplayName("createPublisher guarda la editorial")
		void createPublisher() {
			Publisher publisher = editorial(1L, "Prentice Hall");

			publisherService.createPublisher(publisher);

			verify(publisherRepository).save(publisher);
		}

		@Test
		@DisplayName("updatePublisher también guarda la editorial")
		void updatePublisher() {
			Publisher publisher = editorial(1L, "Prentice Hall");
			publisher.setName("Pearson");

			publisherService.updatePublisher(publisher);

			verify(publisherRepository).save(publisher);
		}
	}

	@Nested
	@DisplayName("Ciclo completo")
	class CicloCompleto {

		@Test
		@DisplayName("vacío → crear → leer → actualizar → borrar → no encontrado")
		void cicloDeVida() {
			Publisher publisher = editorial(1L, "Prentice Hall");
			when(publisherRepository.findAll()).thenReturn(List.of(), List.of(publisher));
			when(publisherRepository.findById(1L))
					.thenReturn(Optional.of(publisher), Optional.of(publisher), Optional.empty());

			assertTrue(publisherService.findAllPublishers().isEmpty());

			publisherService.createPublisher(publisher);
			verify(publisherRepository).save(publisher);
			assertEquals(List.of(publisher), publisherService.findAllPublishers());

			assertEquals("Prentice Hall", publisherService.findPublisherById(1L).getName());

			publisher.setName("Pearson");
			publisherService.updatePublisher(publisher);
			verify(publisherRepository, times(2)).save(publisher);

			publisherService.deletePublisher(1L);
			verify(publisherRepository).deleteById(1L);

			assertThrows(NotFoundException.class, () -> publisherService.findPublisherById(1L));
		}
	}
}
