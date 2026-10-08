package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.support.InMemoryAuthorCatalog;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;

/**
 * Datos aleatorios sobre AuthorServiceImpl. Un assert por propiedad.
 * page, size y el número de autores varían; cada propiedad mira una sola cosa.
 */
class AuthorServiceImplProperties {

	@Property(tries = 25)
	void findPaginatedNuncaSuperaElPageSize(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		AuthorServiceImpl service = serviceCon(autores(n));

		Page<Author> result = service.findPaginated(PageRequest.of(page, size));

		assertTrue(result.getContent().size() <= size);
	}

	@Property(tries = 25)
	void findPaginatedConservaElTotal(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		AuthorServiceImpl service = serviceCon(autores(n));

		Page<Author> result = service.findPaginated(PageRequest.of(page, size));

		assertEquals(n, result.getTotalElements());
	}

	@Property(tries = 25)
	void findPaginatedDevuelveElRecorte(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		List<Author> all = autores(n);
		AuthorServiceImpl service = serviceCon(all);

		Page<Author> result = service.findPaginated(PageRequest.of(page, size));

		int start = page * size;
		List<Author> esperado = start >= n ? List.of() : all.subList(start, Math.min(start + size, n));
		assertEquals(esperado, result.getContent());
	}

	@Property(tries = 20)
	void findAuthorByIdInexistenteMencionaElId(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());
		String mensaje = null;
		try {
			service.findAuthorById(id);
		} catch (NotFoundException ex) {
			mensaje = ex.getMessage();
		}

		assertEquals("Author not found with ID " + id, mensaje);
	}

	@Property(tries = 15)
	void findAuthorByIdInexistenteLanza(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.findAuthorById(id));
	}

	@Property(tries = 20)
	void createSiempreHaceSave(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		Author author = new Author(name, description);

		service.createAuthor(author);

		verify(repository).save(author);
	}

	@Property(tries = 20)
	void updateSiempreHaceSave(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		Author author = new Author(name, description);

		service.updateAuthor(author);

		verify(repository).save(author);
	}

	@Property(tries = 15)
	void trasCrearFindByIdDevuelveElNombre(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorServiceImpl service = new InMemoryAuthorCatalog().service();
		Author author = new Author(name, description);

		service.createAuthor(author);

		assertEquals(name, service.findAuthorById(author.getId()).getName());
	}

	@Property(tries = 15)
	void trasActualizarLaDescripcionEsLaNueva(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorServiceImpl service = new InMemoryAuthorCatalog().service();
		Author author = new Author(name, description);
		service.createAuthor(author);
		Author stored = service.findAuthorById(author.getId());
		stored.setDescription(description + "-upd");

		service.updateAuthor(stored);

		assertEquals(description + "-upd", service.findAuthorById(author.getId()).getDescription());
	}

	@Property(tries = 15)
	void trasBorrarFindAllEstaVacio(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorServiceImpl service = new InMemoryAuthorCatalog().service();
		Author author = new Author(name, description);
		service.createAuthor(author);

		service.deleteAuthor(author.getId());

		assertTrue(service.findAllAuthors().isEmpty());
	}

	@Property(tries = 15)
	void trasBorrarFindByIdLanza(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorServiceImpl service = new InMemoryAuthorCatalog().service();
		Author author = new Author(name, description);
		service.createAuthor(author);
		Long id = author.getId();

		service.deleteAuthor(id);

		assertThrows(NotFoundException.class, () -> service.findAuthorById(id));
	}

	private AuthorServiceImpl serviceCon(List<Author> autores) {
		AuthorRepository repository = mock(AuthorRepository.class);
		when(repository.findAll()).thenReturn(autores);
		return new AuthorServiceImpl(repository);
	}

	private List<Author> autores(int n) {
		List<Author> autores = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			Author author = new Author("author-" + i, "desc");
			author.setId((long) i);
			autores.add(author);
		}
		return autores;
	}
}
