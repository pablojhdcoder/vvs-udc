package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;

class BookServiceImplTest {

	private BookRepository bookRepository;
	private BookServiceImpl bookService;

	@BeforeEach
	void setUp() {
		bookRepository = mock(BookRepository.class);
		bookService = new BookServiceImpl(bookRepository);
	}

	private Book libro(long id, String isbn) {
		Book book = new Book(isbn, "Clean Code", "CC-1", "desc");
		book.setId(id);
		return book;
	}

	@Nested
	@DisplayName("Lectura")
	class Lectura {

		@Test
		@DisplayName("findAllBooks delega en repository.findAll")
		void findAllBooksDevuelveLoDelRepositorio() {
			List<Book> libros = List.of(libro(1L, "111"));
			when(bookRepository.findAll()).thenReturn(libros);

			List<Book> resultado = bookService.findAllBooks();

			assertEquals(libros, resultado);
			verify(bookRepository).findAll();
		}

		@Test
		@DisplayName("searchBooks con keyword llama a repository.search")
		void searchBooksConKeyword() {
			List<Book> encontrados = List.of(libro(1L, "111"));
			when(bookRepository.search("java")).thenReturn(encontrados);

			List<Book> resultado = bookService.searchBooks("java");

			assertEquals(encontrados, resultado);
			verify(bookRepository).search("java");
			verify(bookRepository, never()).findAll();
		}

		@Test
		@DisplayName("searchBooks con null llama a findAll y no a search")
		void searchBooksConNull() {
			List<Book> todos = List.of(libro(1L, "111"));
			when(bookRepository.findAll()).thenReturn(todos);

			List<Book> resultado = bookService.searchBooks(null);

			assertEquals(todos, resultado);
			verify(bookRepository).findAll();
			verify(bookRepository, never()).search(null);
		}
	}

	@Nested
	@DisplayName("findById y delete: existe / no encontrado")
	class PorId {

		@Test
		@DisplayName("findBookById devuelve el libro si el repositorio lo tiene")
		void findBookByIdExistente() {
			Book book = libro(1L, "111");
			when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

			assertEquals(book, bookService.findBookById(1L));
		}

		@Test
		@DisplayName("findBookById lanza NotFoundException si el Optional está vacío")
		void findBookByIdInexistente() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());

			NotFoundException ex = assertThrows(NotFoundException.class, () -> bookService.findBookById(99L));
			assertEquals("Book not found with ID 99", ex.getMessage());
		}

		@Test
		@DisplayName("deleteBook busca y borra por el id del libro encontrado")
		void deleteBookExistente() {
			Book book = libro(1L, "111");
			when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

			bookService.deleteBook(1L);

			verify(bookRepository).findById(1L);
			verify(bookRepository).deleteById(1L);
		}

		@Test
		@DisplayName("deleteBook lanza NotFoundException y no llama a deleteById")
		void deleteBookInexistente() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> bookService.deleteBook(99L));
			verify(bookRepository, never()).deleteById(anyLong());
		}
	}

	@Nested
	@DisplayName("Escritura")
	class Escritura {

		@Test
		@DisplayName("createBook guarda el libro")
		void createBook() {
			Book book = libro(1L, "111");

			bookService.createBook(book);

			verify(bookRepository).save(book);
		}

		@Test
		@DisplayName("updateBook también guarda el libro")
		void updateBook() {
			Book book = libro(1L, "111");
			book.setName("Refactoring");

			bookService.updateBook(book);

			verify(bookRepository).save(book);
		}
	}

	@Nested
	@DisplayName("Paginación")
	class Paginacion {

		@Test
		@DisplayName("página válida recorta la lista y conserva el total")
		void paginaValida() {
			List<Book> todos = List.of(
					libro(1L, "1"),
					libro(2L, "2"),
					libro(3L, "3"),
					libro(4L, "4"),
					libro(5L, "5"));
			when(bookRepository.findAll()).thenReturn(todos);

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 2));

			assertEquals(List.of(todos.get(0), todos.get(1)), pagina.getContent());
			assertEquals(5, pagina.getTotalElements());
			assertEquals(0, pagina.getNumber());
			assertEquals(2, pagina.getSize());
		}

		@Test
		@DisplayName("página fuera de rango queda vacía y el total sigue siendo el de todos")
		void paginaFueraDeRango() {
			List<Book> todos = List.of(libro(1L, "1"), libro(2L, "2"));
			when(bookRepository.findAll()).thenReturn(todos);

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(5, 2));

			assertTrue(pagina.getContent().isEmpty());
			assertEquals(2, pagina.getTotalElements());
		}
	}
}
