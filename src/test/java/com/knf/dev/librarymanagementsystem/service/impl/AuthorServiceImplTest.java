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

/**
 * AuthorServiceImpl no guarda estado: el historial vive en el repositorio.
 * Aquí el repositorio es un mock. Un assert por método.
 *
 * <pre>
 *   [Ausente] --createAuthor--> [Presente]
 *   [Presente] --updateAuthor--> [Presente]
 *   [Presente] --deleteAuthor--> [Ausente]
 *   [Ausente] --findAuthorById--> NotFoundException
 *   [Ausente] --deleteAuthor--> NotFoundException
 * </pre>
 */
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

	private List<Author> cincoAutores() {
		return List.of(
				autor(1L, "A"),
				autor(2L, "B"),
				autor(3L, "C"),
				autor(4L, "D"),
				autor(5L, "E"));
	}

	@Nested
	@DisplayName("findAllAuthors")
	class FindAll {

		@Test
		@DisplayName("catálogo con autores: devuelve la lista del repositorio")
		void devuelveLaLista() {
			List<Author> autores = List.of(autor(1L, "Ada"), autor(2L, "Alan"));
			when(authorRepository.findAll()).thenReturn(autores);

			assertEquals(autores, authorService.findAllAuthors());
		}

		@Test
		@DisplayName("catálogo vacío: devuelve lista vacía")
		void catalogoVacio() {
			when(authorRepository.findAll()).thenReturn(List.of());

			assertTrue(authorService.findAllAuthors().isEmpty());
		}

		@Test
		@DisplayName("delega en findAll")
		void delegaEnFindAll() {
			when(authorRepository.findAll()).thenReturn(List.of());

			authorService.findAllAuthors();

			verify(authorRepository).findAll();
		}
	}

	@Nested
	@DisplayName("findAuthorById")
	class FindById {

		@Test
		@DisplayName("partición existe: devuelve ese autor")
		void existente() {
			Author author = autor(1L, "Ada");
			when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

			assertEquals(author, authorService.findAuthorById(1L));
		}

		@Test
		@DisplayName("partición no existe: lanza NotFoundException")
		void inexistente() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> authorService.findAuthorById(99L));
		}

		@Test
		@DisplayName("el mensaje incluye el id pedido")
		void mensajeIncluyeElId() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());
			String mensaje = null;
			try {
				authorService.findAuthorById(99L);
			} catch (NotFoundException ex) {
				mensaje = ex.getMessage();
			}

			assertEquals("Author not found with ID 99", mensaje);
		}

		@Test
		@DisplayName("frontera id 0 inexistente")
		void idCero() {
			when(authorRepository.findById(0L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> authorService.findAuthorById(0L));
		}
	}

	@Nested
	@DisplayName("createAuthor")
	class Create {

		@Test
		@DisplayName("guarda el autor recibido")
		void guardaElAutor() {
			Author author = autor(1L, "Ada");

			authorService.createAuthor(author);

			verify(authorRepository).save(author);
		}

		@Test
		@DisplayName("guarda otro autor distinto")
		void guardaOtroAutor() {
			Author author = autor(8L, "Alan");

			authorService.createAuthor(author);

			verify(authorRepository).save(author);
		}
	}

	@Nested
	@DisplayName("updateAuthor")
	class Update {

		@Test
		@DisplayName("guarda el autor con el nombre nuevo")
		void guardaNombreNuevo() {
			Author author = autor(1L, "Ada");
			author.setName("Ada Lovelace");

			authorService.updateAuthor(author);

			verify(authorRepository).save(author);
		}

		@Test
		@DisplayName("guarda el autor con la descripción nueva")
		void guardaDescripcionNueva() {
			Author author = autor(1L, "Ada");
			author.setDescription("matemática");

			authorService.updateAuthor(author);

			verify(authorRepository).save(author);
		}
	}

	@Nested
	@DisplayName("deleteAuthor")
	class Delete {

		@Test
		@DisplayName("si existe, borra por el id del autor encontrado")
		void borraSiExiste() {
			Author author = autor(1L, "Ada");
			when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

			authorService.deleteAuthor(1L);

			verify(authorRepository).deleteById(1L);
		}

		@Test
		@DisplayName("si existe, busca antes de borrar")
		void buscaAntesDeBorrar() {
			Author author = autor(1L, "Ada");
			when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

			authorService.deleteAuthor(1L);

			verify(authorRepository).findById(1L);
		}

		@Test
		@DisplayName("si no existe, lanza NotFoundException")
		void inexistenteLanza() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> authorService.deleteAuthor(99L));
		}

		@Test
		@DisplayName("si no existe, no llama a deleteById")
		void inexistenteNoBorra() {
			when(authorRepository.findById(99L)).thenReturn(Optional.empty());

			try {
				authorService.deleteAuthor(99L);
			} catch (NotFoundException ex) {
				// la comprobación es que no se borra
			}

			verify(authorRepository, never()).deleteById(anyLong());
		}
	}

	@Nested
	@DisplayName("findPaginated")
	class Paginacion {

		@Test
		@DisplayName("primera página devuelve los dos primeros")
		void primeraPagina() {
			List<Author> todos = cincoAutores();
			when(authorRepository.findAll()).thenReturn(todos);

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 2));

			assertEquals(List.of(todos.get(0), todos.get(1)), pagina.getContent());
		}

		@Test
		@DisplayName("el total es el de la lista completa")
		void conservaElTotal() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 2));

			assertEquals(5, pagina.getTotalElements());
		}

		@Test
		@DisplayName("conserva el número de página pedido")
		void conservaElNumero() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 2));

			assertEquals(0, pagina.getNumber());
		}

		@Test
		@DisplayName("conserva el tamaño de página pedido")
		void conservaElTamano() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 2));

			assertEquals(2, pagina.getSize());
		}

		@Test
		@DisplayName("última página parcial: un elemento")
		void ultimaPaginaParcial() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(2, 2));

			assertEquals(1, pagina.getContent().size());
		}

		@Test
		@DisplayName("frontera start == tamaño: página vacía")
		void fronteraStartIgualAlTamano() {
			when(authorRepository.findAll()).thenReturn(List.of(autor(1L, "A"), autor(2L, "B")));

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(1, 2));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("página muy por encima del rango: vacía")
		void fueraDeRangoVacia() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(5, 2));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("fuera de rango el total no cambia")
		void fueraDeRangoConservaElTotal() {
			when(authorRepository.findAll()).thenReturn(cincoAutores());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(5, 2));

			assertEquals(5, pagina.getTotalElements());
		}

		@Test
		@DisplayName("catálogo vacío: contenido vacío")
		void catalogoVacio() {
			when(authorRepository.findAll()).thenReturn(List.of());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 5));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("catálogo vacío: total 0")
		void catalogoVacioTotalCero() {
			when(authorRepository.findAll()).thenReturn(List.of());

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 5));

			assertEquals(0, pagina.getTotalElements());
		}

		@Test
		@DisplayName("pageSize mayor que la lista devuelve todos")
		void pageSizeMayorQueLaLista() {
			List<Author> todos = List.of(autor(1L, "A"), autor(2L, "B"));
			when(authorRepository.findAll()).thenReturn(todos);

			Page<Author> pagina = authorService.findPaginated(PageRequest.of(0, 10));

			assertEquals(todos, pagina.getContent());
		}
	}

	@Nested
	@DisplayName("Combinaciones de estado del catálogo")
	class CombinacionesDeEstado {

		private InMemoryAuthorCatalog catalogo;
		private AuthorServiceImpl service;

		@BeforeEach
		void catalogoVacio() {
			catalogo = new InMemoryAuthorCatalog();
			service = catalogo.service();
		}

		private Author crear(String name, String description) {
			Author author = new Author(name, description);
			service.createAuthor(author);
			return author;
		}

		@Test
		@DisplayName("Ausente: findAll está vacío")
		void ausenteFindAllVacio() {
			assertTrue(service.findAllAuthors().isEmpty());
		}

		@Test
		@DisplayName("Ausente: findAuthorById lanza NotFoundException")
		void ausenteFindByIdLanza() {
			assertThrows(NotFoundException.class, () -> service.findAuthorById(1L));
		}

		@Test
		@DisplayName("Ausente --create--> Presente: el catálogo tiene un autor")
		void createDejaUno() {
			crear("Ada", "primera programadora");

			assertEquals(1, catalogo.size());
		}

		@Test
		@DisplayName("Ausente --create--> Presente: findById devuelve el nombre")
		void createLuegoSeLeeElNombre() {
			Author nuevo = crear("Ada", "primera programadora");

			assertEquals("Ada", service.findAuthorById(nuevo.getId()).getName());
		}

		@Test
		@DisplayName("Ausente --create--> Presente: findAll tiene un autor")
		void createApareceEnFindAll() {
			crear("Ada", "primera programadora");

			assertEquals(1, service.findAllAuthors().size());
		}

		@Test
		@DisplayName("Presente --update--> Presente: cambia el nombre")
		void updateCambiaElNombre() {
			Author nuevo = crear("Ada", "primera programadora");
			Author leido = service.findAuthorById(nuevo.getId());
			leido.setName("Ada Lovelace");
			leido.setDescription("matemática");

			service.updateAuthor(leido);

			assertEquals("Ada Lovelace", service.findAuthorById(nuevo.getId()).getName());
		}

		@Test
		@DisplayName("Presente --update--> Presente: cambia la descripción")
		void updateCambiaLaDescripcion() {
			Author nuevo = crear("Ada", "primera programadora");
			Author leido = service.findAuthorById(nuevo.getId());
			leido.setName("Ada Lovelace");
			leido.setDescription("matemática");

			service.updateAuthor(leido);

			assertEquals("matemática", service.findAuthorById(nuevo.getId()).getDescription());
		}

		@Test
		@DisplayName("Presente --update--> Presente: sigue habiendo un autor")
		void updateNoCambiaElTamano() {
			Author nuevo = crear("Ada", "primera programadora");
			Author leido = service.findAuthorById(nuevo.getId());
			leido.setName("Ada Lovelace");

			service.updateAuthor(leido);

			assertEquals(1, catalogo.size());
		}

		@Test
		@DisplayName("Presente --delete--> Ausente: el catálogo queda vacío")
		void deleteDejaCero() {
			Author nuevo = crear("Ada", "primera programadora");

			service.deleteAuthor(nuevo.getId());

			assertEquals(0, catalogo.size());
		}

		@Test
		@DisplayName("Presente --delete--> Ausente: findAll queda vacío")
		void deleteFindAllVacio() {
			Author nuevo = crear("Ada", "primera programadora");

			service.deleteAuthor(nuevo.getId());

			assertTrue(service.findAllAuthors().isEmpty());
		}

		@Test
		@DisplayName("Presente --delete--> Ausente: findById lanza")
		void deleteLuegoFindByIdLanza() {
			Author nuevo = crear("Ada", "primera programadora");

			service.deleteAuthor(nuevo.getId());

			assertThrows(NotFoundException.class, () -> service.findAuthorById(nuevo.getId()));
		}

		@Test
		@DisplayName("Ausente --delete--> NotFoundException")
		void deleteCuandoYaNoEstaLanza() {
			Author nuevo = crear("Ada", "primera programadora");
			service.deleteAuthor(nuevo.getId());

			assertThrows(NotFoundException.class, () -> service.deleteAuthor(nuevo.getId()));
		}

		@Test
		@DisplayName("paginación en catálogo vacío: contenido vacío")
		void paginacionVaciaContenido() {
			Page<Author> vacia = service.findPaginated(PageRequest.of(0, 2));

			assertTrue(vacia.getContent().isEmpty());
		}

		@Test
		@DisplayName("paginación en catálogo vacío: total 0")
		void paginacionVaciaTotal() {
			Page<Author> vacia = service.findPaginated(PageRequest.of(0, 2));

			assertEquals(0, vacia.getTotalElements());
		}

		@Test
		@DisplayName("con tres autores, la primera página tiene dos")
		void primeraPaginaConTres() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> pagina0 = service.findPaginated(PageRequest.of(0, 2));

			assertEquals(2, pagina0.getContent().size());
		}

		@Test
		@DisplayName("con tres autores, el total es tres")
		void totalConTres() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> pagina0 = service.findPaginated(PageRequest.of(0, 2));

			assertEquals(3, pagina0.getTotalElements());
		}

		@Test
		@DisplayName("la segunda página se queda con el resto")
		void segundaPagina() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> pagina1 = service.findPaginated(PageRequest.of(1, 2));

			assertEquals(1, pagina1.getContent().size());
		}

		@Test
		@DisplayName("la segunda página conserva el total")
		void segundaPaginaConservaElTotal() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> pagina1 = service.findPaginated(PageRequest.of(1, 2));

			assertEquals(3, pagina1.getTotalElements());
		}

		@Test
		@DisplayName("página fuera de rango: contenido vacío")
		void fueraDeRangoContenido() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> fuera = service.findPaginated(PageRequest.of(5, 2));

			assertTrue(fuera.getContent().isEmpty());
		}

		@Test
		@DisplayName("página fuera de rango: el total sigue siendo tres")
		void fueraDeRangoTotal() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");

			Page<Author> fuera = service.findPaginated(PageRequest.of(5, 2));

			assertEquals(3, fuera.getTotalElements());
		}

		@Test
		@DisplayName("tras borrar todos, la página vuelve a estar vacía")
		void trasBorrarTodosContenidoVacio() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");
			for (Author author : new ArrayList<>(service.findAllAuthors())) {
				service.deleteAuthor(author.getId());
			}

			Page<Author> otraVezVacia = service.findPaginated(PageRequest.of(0, 2));

			assertTrue(otraVezVacia.getContent().isEmpty());
		}

		@Test
		@DisplayName("tras borrar todos, el total vuelve a cero")
		void trasBorrarTodosTotalCero() {
			crear("A", "d");
			crear("B", "d");
			crear("C", "d");
			for (Author author : new ArrayList<>(service.findAllAuthors())) {
				service.deleteAuthor(author.getId());
			}

			Page<Author> otraVezVacia = service.findPaginated(PageRequest.of(0, 2));

			assertEquals(0, otraVezVacia.getTotalElements());
		}
	}
}
