package com.knf.dev.librarymanagementsystem.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.impl.BookServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Doble de {@link BookRepository} que guarda libros en memoria. El catálogo cambia de estado al
 * crear, actualizar y borrar, para encadenar operaciones sin arrancar Spring.
 */
public final class InMemoryBookCatalog {

  private final List<Book> books = new ArrayList<>();
  private final AtomicLong nextId = new AtomicLong(1);
  private final BookRepository repository;
  private final BookServiceImpl service;

  /** Prepara el mock para que save, find y delete mantengan la lista en memoria. */
  public InMemoryBookCatalog() {
    repository = mock(BookRepository.class);
    service = new BookServiceImpl(repository);
    when(repository.findAll()).thenAnswer(invocation -> new ArrayList<>(books));
    when(repository.findById(anyLong()))
        .thenAnswer(
            invocation -> {
              Long id = invocation.getArgument(0);
              return books.stream().filter(book -> id.equals(book.getId())).findFirst();
            });
    when(repository.save(any(Book.class)))
        .thenAnswer(
            invocation -> {
              Book book = invocation.getArgument(0);
              if (book.getId() == null) {
                book.setId(nextId.getAndIncrement());
              }
              books.removeIf(existing -> book.getId().equals(existing.getId()));
              books.add(book);
              return book;
            });
    doAnswer(
            invocation -> {
              Long id = invocation.getArgument(0);
              books.removeIf(book -> id.equals(book.getId()));
              return null;
            })
        .when(repository)
        .deleteById(anyLong());
  }

  public BookServiceImpl service() {
    return service;
  }
}
