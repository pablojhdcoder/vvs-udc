package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.support.InMemoryAuthorCatalog;

class AuthorServiceImplTest {

	private AuthorRepository authorRepository;
	private AuthorServiceImpl authorService;

	@BeforeEach
	void setUp() {
		authorRepository = mock(AuthorRepository.class);
		authorService = new AuthorServiceImpl(authorRepository);
	}

	private Author autor(long id, String name) {
		Author author = new Author(name, "desc");
		author.setId(id);
		return author;
	}

	@Nested
	@DisplayName("Lectura")
	class Lectura {

		@Test
		@DisplayName("findAllAuthors delega en repository.findAll")
		void findAllAuthorsDevuelveLoDelRepositorio() {
			List<Author> autores = List.of(autor(1L, "Ada"), autor(2L, "Alan"));
			when(authorRepository.findAll()).thenReturn(autores);

			List<Author> resultado = authorService.findAllAuthors();

			assertEquals(autores, resultado);
			verify(authorRepository).findAll();
		}
	}

	@Nested
	@DisplayName("findById y delete: existe / no encontrado")
	class PorId {

		@Test
		@DisplayName("findAuthorById devuelve el autor si el repositorio lo tiene")
		void findAuthorByIdExistente() {
			Author author = autor(1L, "Ada");
			when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

			assertEquals(author, authorService.findAuthorById(1L));
		}

		@Test
		@DisplayName("findAuthorById lanza NotFoundException si el Optional está vacío")
		void findAuthorByIdInexistente() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());

			NotFoundException ex = assertThrows(NotFoundException.class, () -> authorService.findAuthorById(99L));
			assertEquals("Author not found with ID 99", ex.getMessage());
		}

		@Test
		@DisplayName("deleteAuthor busca y borra por el id del autor encontrado")
		void deleteAuthorExistente() {
			Author author = autor(1L, "Ada");
			when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

			authorService.deleteAuthor(1L);

			verify(authorRepository).findById(1L);
			verify(authorRepository).deleteById(1L);
		}

		@Test
		@DisplayName("deleteAuthor lanza NotFoundException y no llama a deleteById")
		void deleteAuthorInexistente() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> authorService.deleteAuthor(99L));
			verify(authorRepository, never()).deleteById(anyLong());
		}
	}

	@Nested
	@DisplayName("Escritura")
	class Escritura {

		@Test
		@DisplayName("createAuthor guarda el autor")
		void createAuthor() {
			Author author = autor(1L, "Ada");

			authorService.createAuthor(author);

			verify(authorRepository).save(author);
		}

		@Test
		@DisplayName("updateAuthor también guarda el autor")
		void updateAuthor() {
			Author author = autor(1L, "Ada");
			author.setName("Ada Lovelace");

			authorService.updateAuthor(author);

			verify(authorRepository).save(author);
		}
	}

	@Nested
	@DisplayName("Paginación")
	class Paginacion {

		@Test
		@DisplayName("página válida recorta la lista y conserva el total")
		void paginaValida() {
			List<Author> todos = List.of(
					autor(1L, "A"),
					autor(2L, "B"),
					autor(3L, "C"),
					autor(4L, "D"),
					autor(5L, "E"));
			when(authorRepository.findAll()).thenReturn(todos);

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 2));

			assertEquals(List.of(todos.get(0), todos.get(1)), pagina.getContent());
			assertEquals(5, pagina.getTotalElements());
			assertEquals(0, pagina.getNumber());
			assertEquals(2, pagina.getSize());
		}

		@Test
		@DisplayName("página fuera de rango queda vacía y el total sigue siendo el de todos")
		void paginaFueraDeRango() {
			List<Author> todos = List.of(autor(1L, "A"), autor(2L, "B"));
			when(authorRepository.findAll()).thenReturn(todos);

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(5, 2));

			assertTrue(pagina.getContent().isEmpty());
			assertEquals(2, pagina.getTotalElements());
		}
	}

	@Nested
	@DisplayName("Combinaciones de estado del catálogo")
	class CombinacionesDeEstado {

		private InMemoryAuthorCatalog catalogo;

		@BeforeEach
		void catalogoVacio() {
			catalogo = new InMemoryAuthorCatalog();
		}

		@Test
		@DisplayName("vacío → crear → leer → actualizar → borrar → ya no está")
		void cicloCompletoDelAutor() {
			AuthorServiceImpl service = catalogo.service();

			assertTrue(service.findAllAuthors().isEmpty());
			assertThrows(NotFoundException.class, () -> service.findAuthorById(1L));

			Author nuevo = new Author("Ada", "primera programadora");
			service.createAuthor(nuevo);
			assertEquals(1, catalogo.size());
			assertEquals("Ada", service.findAuthorById(nuevo.getId()).getName());
			assertEquals(1, service.findAllAuthors().size());

			Author leido = service.findAuthorById(nuevo.getId());
			leido.setName("Ada Lovelace");
			leido.setDescription("matemática");
			service.updateAuthor(leido);

			Author actualizado = service.findAuthorById(nuevo.getId());
			assertEquals("Ada Lovelace", actualizado.getName());
			assertEquals("matemática", actualizado.getDescription());
			assertEquals(1, catalogo.size());

			service.deleteAuthor(nuevo.getId());
			assertEquals(0, catalogo.size());
			assertTrue(service.findAllAuthors().isEmpty());
			assertThrows(NotFoundException.class, () -> service.findAuthorById(nuevo.getId()));
			assertThrows(NotFoundException.class, () -> service.deleteAuthor(nuevo.getId()));
		}

		@Test
		@DisplayName("paginación recorre catálogo vacío, con autores y otra vez vacío")
		void paginacionSegunElEstadoDelCatalogo() {
			AuthorServiceImpl service = catalogo.service();
			PageRequest primera = PageRequest.of(0, 2);

			Page<Author> vacia = service.findPaginated(primera);
			assertTrue(vacia.getContent().isEmpty());
			assertEquals(0, vacia.getTotalElements());

			service.createAuthor(new Author("A", "d"));
			service.createAuthor(new Author("B", "d"));
			service.createAuthor(new Author("C", "d"));

			Page<Author> pagina0 = service.findPaginated(primera);
			assertEquals(2, pagina0.getContent().size());
			assertEquals(3, pagina0.getTotalElements());

			Page<Author> pagina1 = service.findPaginated(PageRequest.of(1, 2));
			assertEquals(1, pagina1.getContent().size());
			assertEquals(3, pagina1.getTotalElements());

			Page<Author> fuera = service.findPaginated(PageRequest.of(5, 2));
			assertTrue(fuera.getContent().isEmpty());
			assertEquals(3, fuera.getTotalElements());

			for (Author author : new ArrayList<>(service.findAllAuthors())) {
				service.deleteAuthor(author.getId());
			}

			Page<Author> otraVezVacia = service.findPaginated(primera);
			assertTrue(otraVezVacia.getContent().isEmpty());
			assertEquals(0, otraVezVacia.getTotalElements());
		}
	}
}
