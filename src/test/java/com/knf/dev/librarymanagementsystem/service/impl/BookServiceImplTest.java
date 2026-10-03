package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.support.InMemoryBookCatalog;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * BookServiceImpl no guarda estado: el historial vive en el repositorio. Cada método comprueba una
 * cosa. El ciclo del catálogo usa un mock con memoria y agrupa las comprobaciones en assertAll.
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
    return List.of(libro(1L, "1"), libro(2L, "2"), libro(3L, "3"), libro(4L, "4"), libro(5L, "5"));
  }

  @Nested
  @DisplayName("findAllBooks")
  class FindAll {

    @Test
    @DisplayName("catálogo con libros: devuelve la lista del repositorio")
    void devolveLista() {
      List<Book> libros = List.of(libro(1L, "111"), libro(2L, "222"));
      when(bookRepository.findAll()).thenReturn(libros);

      assertEquals(libros, bookService.findAllBooks());
    }

    @Test
    @DisplayName("catálogo vacío: devuelve lista vacía")
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
    @DisplayName("keyword no nula: devuelve el resultado de search")
    void keywordDevolveResultado() {
      List<Book> encontrados = List.of(libro(1L, "111"));
      when(bookRepository.search("java")).thenReturn(encontrados);

      assertEquals(encontrados, bookService.searchBooks("java"));
    }

    @Test
    @DisplayName("keyword vacía no es null: también usa search")
    void keywordBaleiraUsaSearch() {
      when(bookRepository.search("")).thenReturn(List.of());

      bookService.searchBooks("");

      verify(bookRepository).search("");
    }

    @Test
    @DisplayName("keyword no nula no llama a findAll")
    void keywordNonChamaFindAll() {
      when(bookRepository.search("java")).thenReturn(List.of());

      bookService.searchBooks("java");

      verify(bookRepository, never()).findAll();
    }

    @Test
    @DisplayName("keyword null: devuelve findAll")
    void nullDevolveFindAll() {
      List<Book> todos = List.of(libro(1L, "111"));
      when(bookRepository.findAll()).thenReturn(todos);

      assertEquals(todos, bookService.searchBooks(null));
    }

    @Test
    @DisplayName("keyword null no llama a search")
    void nullNonChamaSearch() {
      when(bookRepository.findAll()).thenReturn(List.of());

      bookService.searchBooks(null);

      verify(bookRepository, never()).search(null);
    }

    @Test
    @DisplayName("keyword null y catálogo vacío")
    void nullConCatalogoBaleiro() {
      when(bookRepository.findAll()).thenReturn(List.of());

      assertTrue(bookService.searchBooks(null).isEmpty());
    }
  }

  @Nested
  @DisplayName("findBookById")
  class FindById {

    @Test
    @DisplayName("partición existe: devuelve ese libro")
    void existente() {
      Book book = libro(1L, "111");
      when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

      assertEquals(book, bookService.findBookById(1L));
    }

    @Test
    @DisplayName("partición no existe: lanza NotFoundException")
    void inexistente() {
      when(bookRepository.findById(99L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookService.findBookById(99L));
    }

    @Test
    @DisplayName("el mensaje incluye el id pedido")
    void mensaxeInclueId() {
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
    @DisplayName("frontera id 0 inexistente")
    void idCero() {
      when(bookRepository.findById(0L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookService.findBookById(0L));
    }
  }

  @Nested
  @DisplayName("createBook")
  class Create {

    @Test
    @DisplayName("guarda el libro recibido")
    void gardaLibro() {
      Book book = libro(1L, "111");

      bookService.createBook(book);

      verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("guarda otro libro distinto")
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
    @DisplayName("guarda el libro con el nombre nuevo")
    void gardaNomeNovo() {
      Book book = libro(1L, "111");
      book.setName("Refactoring");

      bookService.updateBook(book);

      verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("guarda el libro con el isbn nuevo")
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
    @DisplayName("si existe, borra por el id del libro encontrado")
    void borraSeExiste() {
      Book book = libro(1L, "111");
      when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

      bookService.deleteBook(1L);

      verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("borra el id del libro encontrado, aunque la búsqueda usara otro")
    void borraIdDaEntidade() {
      Book book = libro(9L, "111");
      when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

      bookService.deleteBook(5L);

      verify(bookRepository).deleteById(9L);
    }

    @Test
    @DisplayName("si existe, busca antes de borrar")
    void buscaAntesDeBorrar() {
      Book book = libro(1L, "111");
      when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

      bookService.deleteBook(1L);

      verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("si no existe, lanza NotFoundException")
    void inexistenteLanza() {
      when(bookRepository.findById(99L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookService.deleteBook(99L));
    }

    @Test
    @DisplayName("si no existe, no llama a deleteById")
    void inexistenteNonBorra() {
      when(bookRepository.findById(99L)).thenReturn(Optional.empty());

      try {
        bookService.deleteBook(99L);
      } catch (NotFoundException ex) {
        // la comprobación es que no se borra
      }

      verify(bookRepository, never()).deleteById(anyLong());
    }
  }

  @Nested
  @DisplayName("findPaginated")
  class Paginacion {

    @Test
    @DisplayName("primera página devuelve los dos primeros")
    void primeiraPaxina() {
      List<Book> todos = cincoLibros();
      when(bookRepository.findAll()).thenReturn(todos);

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 2));

      assertEquals(List.of(todos.get(0), todos.get(1)), pagina.getContent());
    }

    @Test
    @DisplayName("el total es el de la lista completa")
    void conservaTotal() {
      when(bookRepository.findAll()).thenReturn(cincoLibros());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 2));

      assertEquals(5, pagina.getTotalElements());
    }

    @Test
    @DisplayName("última página parcial: un elemento")
    void ultimaPaxinaParcial() {
      when(bookRepository.findAll()).thenReturn(cincoLibros());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(2, 2));

      assertEquals(1, pagina.getContent().size());
    }

    @Test
    @DisplayName("frontera start == tamaño: página vacía")
    void fronteiraStartIgualAoTamano() {
      when(bookRepository.findAll()).thenReturn(List.of(libro(1L, "1"), libro(2L, "2")));

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(1, 2));

      assertTrue(pagina.getContent().isEmpty());
    }

    @Test
    @DisplayName("página muy por encima del rango: vacía")
    void foraDeRangoBaleira() {
      when(bookRepository.findAll()).thenReturn(cincoLibros());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(5, 2));

      assertTrue(pagina.getContent().isEmpty());
    }

    @Test
    @DisplayName("fuera de rango el total no cambia")
    void foraDeRangoConservaTotal() {
      when(bookRepository.findAll()).thenReturn(cincoLibros());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(5, 2));

      assertEquals(5, pagina.getTotalElements());
    }

    @Test
    @DisplayName("catálogo vacío: contenido vacío")
    void catalogoBaleiro() {
      when(bookRepository.findAll()).thenReturn(List.of());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 5));

      assertTrue(pagina.getContent().isEmpty());
    }

    @Test
    @DisplayName("catálogo vacío: total 0")
    void catalogoBaleiroTotalCero() {
      when(bookRepository.findAll()).thenReturn(List.of());

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 5));

      assertEquals(0, pagina.getTotalElements());
    }

    @Test
    @DisplayName("pageSize mayor que la lista devuelve todos")
    void pageSizeMaiorCaLista() {
      List<Book> todos = List.of(libro(1L, "1"), libro(2L, "2"));
      when(bookRepository.findAll()).thenReturn(todos);

      Page<Book> pagina = bookService.findPaginated(PageRequest.of(0, 10));

      assertEquals(todos, pagina.getContent());
    }
  }

  @Nested
  @DisplayName("Ciclo del catálogo")
  class Ciclo {

    @Test
    @DisplayName("vacío → crear → leer → actualizar → borrar → no encontrado")
    void vacioCrearLerActualizarBorrar() {
      InMemoryBookCatalog catalog = new InMemoryBookCatalog();
      BookServiceImpl service = catalog.service();
      Book book = new Book("978-1", "Clean Code", "CC-1", "desc");

      assertAll(
          () -> assertTrue(service.findAllBooks().isEmpty()),
          () -> service.createBook(book),
          () -> assertEquals("Clean Code", service.findBookById(book.getId()).getName()),
          () -> {
            book.setName("Refactoring");
            service.updateBook(book);
          },
          () -> assertEquals("Refactoring", service.findBookById(book.getId()).getName()),
          () -> service.deleteBook(book.getId()),
          () -> assertTrue(service.findAllBooks().isEmpty()),
          () -> assertThrows(NotFoundException.class, () -> service.findBookById(book.getId())));
    }
  }
}
