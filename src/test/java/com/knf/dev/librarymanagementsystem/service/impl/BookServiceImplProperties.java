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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/** Datos aleatorios sobre BookServiceImpl. Un assert por propiedade. */
class BookServiceImplProperties {

  @Property(tries = 25)
  void findPaginatedNuncaSuperaPageSize(
      @ForAll @IntRange(min = 0, max = 25) int n,
      @ForAll @IntRange(min = 0, max = 8) int page,
      @ForAll @IntRange(min = 1, max = 10) int size) {
    BookServiceImpl service = serviceCon(libros(n));

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    assertTrue(result.getContent().size() <= size);
  }

  @Property(tries = 25)
  void findPaginatedConservaTotal(
      @ForAll @IntRange(min = 0, max = 25) int n,
      @ForAll @IntRange(min = 0, max = 8) int page,
      @ForAll @IntRange(min = 1, max = 10) int size) {
    BookServiceImpl service = serviceCon(libros(n));

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    assertEquals(n, result.getTotalElements());
  }

  @Property(tries = 20)
  void findBookByIdInexistenteMencionaId(@ForAll @LongRange(min = 1, max = 10_000) long id) {
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

  @Property(tries = 15)
  void updateSempreFaiSave(@ForAll @StringLength(min = 1, max = 40) String isbn) {
    BookRepository repository = mock(BookRepository.class);
    BookServiceImpl service = new BookServiceImpl(repository);
    Book book = new Book(isbn, "nome", "serial", "desc");

    service.updateBook(book);

    verify(repository).save(book);
  }

  @Property(tries = 15)
  void deleteInexistenteLanza(@ForAll @LongRange(min = 1, max = 10_000) long id) {
    BookRepository repository = mock(BookRepository.class);
    BookServiceImpl service = new BookServiceImpl(repository);
    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.deleteBook(id));
  }

  @Property(tries = 15)
  void deleteInexistenteNonChamaDeleteById(@ForAll @LongRange(min = 1, max = 10_000) long id) {
    BookRepository repository = mock(BookRepository.class);
    BookServiceImpl service = new BookServiceImpl(repository);
    when(repository.findById(id)).thenReturn(Optional.empty());

    try {
      service.deleteBook(id);
    } catch (NotFoundException ex) {
      // a comprobación é que non se borra
    }

    verify(repository, never()).deleteById(anyLong());
  }

  @Property(tries = 15)
  void searchNullNonChamaSearch(@ForAll @IntRange(min = 0, max = 8) int n) {
    BookRepository repository = mock(BookRepository.class);
    BookServiceImpl service = new BookServiceImpl(repository);
    when(repository.findAll()).thenReturn(libros(n));

    service.searchBooks(null);

    verify(repository, never()).search(null);
  }

  @Property(tries = 20)
  void findPaginatedRecortaSublista(
      @ForAll @IntRange(min = 0, max = 25) int n,
      @ForAll @IntRange(min = 0, max = 8) int page,
      @ForAll @IntRange(min = 1, max = 10) int size) {
    List<Book> all = libros(n);
    BookServiceImpl service = serviceCon(all);

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    int start = page * size;
    List<Book> expected = start >= n ? List.of() : all.subList(start, Math.min(start + size, n));
    assertEquals(expected, result.getContent());
  }

  @Property(tries = 15)
  void fronteiraStartIgualAoNumeroDeLibros(
      @ForAll @IntRange(min = 1, max = 8) int size, @ForAll @IntRange(min = 1, max = 4) int page) {
    int n = page * size;
    BookServiceImpl service = serviceCon(libros(n));

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    assertTrue(result.getContent().isEmpty());
  }

  @Property(tries = 15)
  void fronteiraUnPorRibaDoRangoQuedaBaleira(
      @ForAll @IntRange(min = 1, max = 8) int size, @ForAll @IntRange(min = 1, max = 4) int page) {
    int n = page * size - 1;
    BookServiceImpl service = serviceCon(libros(n));

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    assertTrue(result.getContent().isEmpty());
  }

  @Property(tries = 15)
  void fronteiraUltimaPaxinaParcialTenUnElemento(
      @ForAll @IntRange(min = 1, max = 8) int size, @ForAll @IntRange(min = 0, max = 4) int page) {
    int n = page * size + 1;
    BookServiceImpl service = serviceCon(libros(n));

    Page<Book> result = service.findPaginated(PageRequest.of(page, size));

    assertEquals(1, result.getContent().size());
  }

  @Property(tries = 15)
  void cicloVacioCrearLerActualizarBorrar(
      @ForAll @StringLength(min = 1, max = 20) String isbn,
      @ForAll @StringLength(min = 1, max = 40) String name) {
    InMemoryBookCatalog catalog = new InMemoryBookCatalog();
    BookServiceImpl service = catalog.service();
    Book book = new Book(isbn, name, "serial", "desc");

    assertAll(
        () -> assertTrue(service.findAllBooks().isEmpty()),
        () -> service.createBook(book),
        () -> assertEquals(name, service.findBookById(book.getId()).getName()),
        () -> {
          book.setName(name + "-upd");
          service.updateBook(book);
        },
        () -> assertEquals(name + "-upd", service.findBookById(book.getId()).getName()),
        () -> service.deleteBook(book.getId()),
        () -> assertTrue(service.findAllBooks().isEmpty()),
        () -> assertThrows(NotFoundException.class, () -> service.findBookById(book.getId())));
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
