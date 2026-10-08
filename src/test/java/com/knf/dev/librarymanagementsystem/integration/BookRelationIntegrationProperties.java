package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import javax.persistence.EntityManager;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.spring.JqwikSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Nombres aleatorios de las relaciones de Book, leídos otra vez desde H2. */
@JqwikSpringSupport
@SpringBootTest
@Transactional
class BookRelationIntegrationProperties {

  @Autowired private BookRepository bookRepository;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private PublisherRepository publisherRepository;
  @Autowired private EntityManager entityManager;

  private void vaciar() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
    categoryRepository.deleteAll();
    publisherRepository.deleteAll();
  }

  private Book guardar(String isbn) {
    return bookRepository.save(new Book(isbn, "Nombre", "SER-1", "descripcion"));
  }

  private void recargar() {
    entityManager.flush();
    entityManager.clear();
  }

  @Property(tries = 4)
  @Label("un autor con nombre aleatorio sigue en el libro tras recargar")
  void autorAleatorioSobrevive(
      @ForAll @AlphaChars @StringLength(min = 1, max = 12) String sufijo) {
    vaciar();
    String nombre = "A" + sufijo;
    Author author = authorRepository.save(new Author(nombre, "descripcion"));
    Book book = guardar("RA" + sufijo);
    book.addAuthors(author);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals(nombre, stored.getAuthors().iterator().next().getName());
  }

  @Property(tries = 4)
  @Label("una categoría con nombre aleatorio sigue en el libro tras recargar")
  void categoriaAleatoriaSobrevive(
      @ForAll @AlphaChars @StringLength(min = 1, max = 12) String sufijo) {
    vaciar();
    String nombre = "C" + sufijo;
    Category category = categoryRepository.save(new Category(nombre));
    Book book = guardar("RC" + sufijo);
    book.addCategories(category);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals(nombre, stored.getCategories().iterator().next().getName());
  }

  @Property(tries = 4)
  @Label("una editorial con nombre aleatorio sigue en el libro tras recargar")
  void editorialAleatoriaSobrevive(
      @ForAll @AlphaChars @StringLength(min = 1, max = 12) String sufijo) {
    vaciar();
    String nombre = "E" + sufijo;
    Publisher publisher = publisherRepository.save(new Publisher(nombre));
    Book book = guardar("RE" + sufijo);
    book.addPublishers(publisher);
    bookRepository.save(book);
    recargar();

    Book stored = bookRepository.findById(book.getId()).orElseThrow();
    assertEquals(nombre, stored.getPublishers().iterator().next().getName());
  }
}
