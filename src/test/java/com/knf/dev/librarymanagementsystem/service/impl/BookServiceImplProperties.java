package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;

/**
 * Datos aleatorios sobre BookServiceImpl. Un assert por propiedade.
 */
class BookServiceImplProperties {

	@Property(tries = 25)
	void findPaginatedNuncaSuperaOPageSize(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		BookServiceImpl service = serviceCon(libros(n));

		Page<Book> result = service.findPaginated(PageRequest.of(page, size));

		assertTrue(result.getContent().size() <= size);
	}

	@Property(tries = 25)
	void findPaginatedConservaOTotal(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		BookServiceImpl service = serviceCon(libros(n));

		Page<Book> result = service.findPaginated(PageRequest.of(page, size));

		assertEquals(n, result.getTotalElements());
	}

	@Property(tries = 20)
	void findBookByIdInexistenteMencionaOId(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		BookRepository repository = mock(BookRepository.class);
		BookServiceImpl service = new BookServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());
		String mensaxe = null;
		try {
			service.findBookById(id);
		} catch (NotFoundException ex) {
			mensaxe = ex.getMessage();
		}

		assertEquals("Book not found with ID " + id, mensaxe);
	}

	@Property(tries = 15)
	void findBookByIdInexistenteLanza(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		BookRepository repository = mock(BookRepository.class);
		BookServiceImpl service = new BookServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.findBookById(id));
	}

	@Property(tries = 20)
	void searchConKeywordNonChamaFindAll(@ForAll @StringLength(max = 30) String keyword) {
		BookRepository repository = mock(BookRepository.class);
		BookServiceImpl service = new BookServiceImpl(repository);
		when(repository.search(keyword)).thenReturn(List.of());

		service.searchBooks(keyword);

		verify(repository, never()).findAll();
	}

	@Property(tries = 15)
	void createSempreFaiSave(@ForAll @StringLength(min = 1, max = 40) String isbn) {
		BookRepository repository = mock(BookRepository.class);
		BookServiceImpl service = new BookServiceImpl(repository);
		Book book = new Book(isbn, "nome", "serial", "desc");

		service.createBook(book);

		verify(repository).save(book);
	}

	private BookServiceImpl serviceCon(List<Book> libros) {
		BookRepository repository = mock(BookRepository.class);
		when(repository.findAll()).thenReturn(libros);
		return new BookServiceImpl(repository);
	}

	private List<Book> libros(int n) {
		List<Book> libros = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			Book book = new Book("isbn-" + i, "nome-" + i, "s-" + i, "desc");
			book.setId((long) i);
			libros.add(book);
		}
		return libros;
	}
}
