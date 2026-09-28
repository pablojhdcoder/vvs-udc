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

/**
 * BookServiceImpl non garda estado: o historial vive no repositorio.
 * Aquí o repositorio é un mock. As transicións do catálogo están na proba de integración.
 */
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

	private List<Book> cincoLibros() {
		return List.of(
				libro(1L, "1"),
				libro(2L, "2"),
				libro(3L, "3"),
				libro(4L, "4"),
				libro(5L, "5"));
	}

	@Nested
	@DisplayName("findAllBooks")
	class FindAll {

		@Test
		@DisplayName("catálogo con libros: devolve a lista do repositorio")
		void devolveALista() {
			List<Book> libros = List.of(libro(1L, "111"), libro(2L, "222"));
			when(bookRepository.findAll()).thenReturn(libros);

			assertEquals(libros, bookService.findAllBooks());
		}

		@Test
		@DisplayName("catálogo baleiro: devolve lista baleira")
		void catalogoBaleiro() {
			when(bookRepository.findAll()).thenReturn(List.of());

			assertTrue(bookService.findAllBooks().isEmpty());
		}

		@Test
		@DisplayName("delega en findAll")
		void delegaEnFindAll() {
			when(bookRepository.findAll()).thenReturn(List.of());

			bookService.findAllBooks();

			verify(bookRepository).findAll();
		}
	}

	@Nested
	@DisplayName("searchBooks")
	class Search {

		@Test
		@DisplayName("keyword non nula: devolve o resultado de search")
		void keywordDevolveOResultado() {
			List<Book> encontrados = List.of(libro(1L, "111"));
			when(bookRepository.search("java")).thenReturn(encontrados);

			assertEquals(encontrados, bookService.searchBooks("java"));
		}

		@Test
		@DisplayName("keyword baleira non é null: tamén usa search")
		void keywordBaleiraUsaSearch() {
			when(bookRepository.search("")).thenReturn(List.of());

			bookService.searchBooks("");

			verify(bookRepository).search("");
		}

		@Test
		@DisplayName("keyword non nula non chama a findAll")
		void keywordNonChamaFindAll() {
			when(bookRepository.search("java")).thenReturn(List.of());

			bookService.searchBooks("java");

			verify(bookRepository, never()).findAll();
		}

		@Test
		@DisplayName("keyword null: devolve findAll")
		void nullDevolveFindAll() {
			List<Book> todos = List.of(libro(1L, "111"));
			when(bookRepository.findAll()).thenReturn(todos);

			assertEquals(todos, bookService.searchBooks(null));
		}

		@Test
		@DisplayName("keyword null non chama a search")
		void nullNonChamaSearch() {
			when(bookRepository.findAll()).thenReturn(List.of());

			bookService.searchBooks(null);

			verify(bookRepository, never()).search(null);
		}

		@Test
		@DisplayName("keyword null e catálogo baleiro")
		void nullConCatalogoBaleiro() {
			when(bookRepository.findAll()).thenReturn(List.of());

			assertTrue(bookService.searchBooks(null).isEmpty());
		}
	}

	@Nested
	@DisplayName("findBookById")
	class FindById {

		@Test
		@DisplayName("partición existe: devolve ese libro")
		void existente() {
			Book book = libro(1L, "111");
			when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

			assertEquals(book, bookService.findBookById(1L));
		}

		@Test
		@DisplayName("partición non existe: lanza NotFoundException")
		void inexistente() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> bookService.findBookById(99L));
		}

		@Test
		@DisplayName("a mensaxe inclúe o id pedido")
		void mensaxeInclueOId() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());
			String mensaxe = null;
			try {
				bookService.findBookById(99L);
			} catch (NotFoundException ex) {
				mensaxe = ex.getMessage();
			}

			assertEquals("Book not found with ID 99", mensaxe);
		}

		@Test
		@DisplayName("fronteira id 0 inexistente")
		void idCero() {
			when(bookRepository.findById(0L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> bookService.findBookById(0L));
		}
	}

	@Nested
	@DisplayName("createBook")
	class Create {

		@Test
		@DisplayName("garda o libro recibido")
		void gardaOLibro() {
			Book book = libro(1L, "111");

			bookService.createBook(book);

			verify(bookRepository).save(book);
		}

		@Test
		@DisplayName("garda outro libro distinto")
		void gardaOutroLibro() {
			Book book = libro(8L, "888");

			bookService.createBook(book);

			verify(bookRepository).save(book);
		}
	}

	@Nested
	@DisplayName("updateBook")
	class Update {

		@Test
		@DisplayName("garda o libro co nome novo")
		void gardaNomeNovo() {
			Book book = libro(1L, "111");
			book.setName("Refactoring");

			bookService.updateBook(book);

			verify(bookRepository).save(book);
		}

		@Test
		@DisplayName("garda o libro co isbn novo")
		void gardaIsbnNovo() {
			Book book = libro(1L, "111");
			book.setIsbn("999");

			bookService.updateBook(book);

			verify(bookRepository).save(book);
		}
	}

	@Nested
	@DisplayName("deleteBook")
	class Delete {

		@Test
		@DisplayName("se existe, borra polo id do libro atopado")
		void borraSeExiste() {
			Book book = libro(1L, "111");
			when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

			bookService.deleteBook(1L);

			verify(bookRepository).deleteById(1L);
		}

		@Test
		@DisplayName("se existe, busca antes de borrar")
		void buscaAntesDeBorrar() {
			Book book = libro(1L, "111");
			when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

			bookService.deleteBook(1L);

			verify(bookRepository).findById(1L);
		}

		@Test
		@DisplayName("se non existe, lanza NotFoundException")
		void inexistenteLanza() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());

			assertThrows(NotFoundException.class, () -> bookService.deleteBook(99L));
		}

		@Test
		@DisplayName("se non existe, non chama a deleteById")
		void inexistenteNonBorra() {
			when(bookRepository.findById(99L)).thenReturn(Optional.empty());

			try {
				bookService.deleteBook(99L);
			} catch (NotFoundException ex) {
				// a comprobación é que non se borra
			}

			verify(bookRepository, never()).deleteById(anyLong());
		}
	}

	@Nested
	@DisplayName("findPaginated")
	class Paginacion {

		@Test
		@DisplayName("primeira páxina devolve os dous primeiros")
		void primeiraPaxina() {
			List<Book> todos = cincoLibros();
			when(bookRepository.findAll()).thenReturn(todos);

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 2));

			assertEquals(List.of(todos.get(0), todos.get(1)), pagina.getContent());
		}

		@Test
		@DisplayName("o total é o da lista completa")
		void conservaOTotal() {
			when(bookRepository.findAll()).thenReturn(cincoLibros());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 2));

			assertEquals(5, pagina.getTotalElements());
		}

		@Test
		@DisplayName("última páxina parcial: un elemento")
		void ultimaPaxinaParcial() {
			when(bookRepository.findAll()).thenReturn(cincoLibros());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(2, 2));

			assertEquals(1, pagina.getContent().size());
		}

		@Test
		@DisplayName("fronteira start == tamaño: páxina baleira")
		void fronteiraStartIgualAoTamano() {
			when(bookRepository.findAll()).thenReturn(List.of(libro(1L, "1"), libro(2L, "2")));

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(1, 2));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("páxina moi por riba do rango: baleira")
		void foraDeRangoBaleira() {
			when(bookRepository.findAll()).thenReturn(cincoLibros());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(5, 2));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("fóra de rango o total non cambia")
		void foraDeRangoConservaOTotal() {
			when(bookRepository.findAll()).thenReturn(cincoLibros());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(5, 2));

			assertEquals(5, pagina.getTotalElements());
		}

		@Test
		@DisplayName("catálogo baleiro: contido baleiro")
		void catalogoBaleiro() {
			when(bookRepository.findAll()).thenReturn(List.of());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 5));

			assertTrue(pagina.getContent().isEmpty());
		}

		@Test
		@DisplayName("catálogo baleiro: total 0")
		void catalogoBaleiroTotalCero() {
			when(bookRepository.findAll()).thenReturn(List.of());

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 5));

			assertEquals(0, pagina.getTotalElements());
		}

		@Test
		@DisplayName("pageSize maior que a lista devolve todos")
		void pageSizeMaiorQueALista() {
			List<Book> todos = List.of(libro(1L, "1"), libro(2L, "2"));
			when(bookRepository.findAll()).thenReturn(todos);

			Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 10));

			assertEquals(todos, pagina.getContent());
		}
	}
}
