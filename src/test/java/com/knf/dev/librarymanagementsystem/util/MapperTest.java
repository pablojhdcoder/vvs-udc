package com.knf.dev.librarymanagementsystem.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.vo.AuthorRecord;
import com.knf.dev.librarymanagementsystem.vo.BookRecord;
import com.knf.dev.librarymanagementsystem.vo.CategoryRecord;
import com.knf.dev.librarymanagementsystem.vo.PublisherRecord;


class MapperTest {

  private Book book(long id, String isbn, String name, String serialName, String description) {
    Book book = new Book(isbn, name, serialName, description);
    book.setId(id);
    return book;
  }

  private Author author(long id, String name, String description) {
    Author author = new Author(name, description);
    author.setId(id);
    return author;
  }

  private Category category(long id, String name) {
    Category category = new Category(name);
    category.setId(id);
    return category;
  }

  private Publisher publisher(long id, String name) {
    Publisher publisher = new Publisher(name);
    publisher.setId(id);
    return publisher;
  }


  @Test
  void testBookModelToVoWithEmptyList() {
    List<BookRecord> result = Mapper.bookModelToVo(List.of());

    assertAll(
        "Comprobar que una lista vacía de libros se convierte en una lista vacía",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testBookModelToVoWithOneBook() {
    List<BookRecord> result = Mapper.bookModelToVo(
        List.of(book(1L, "111", "Clean Code", "CC-1", "Código limpio")));

    assertAll(
        "Comprobar que un libro se convierte copiando id, isbn, name, serialName y description",
        () -> assertEquals(List.of(new BookRecord(1L, "111", "Clean Code", "CC-1", "Código limpio")), result)
    );
  }

  @Test
  void testBookModelToVoWithTwoBooks() {
    List<BookRecord> result = Mapper.bookModelToVo(List.of(
        book(1L, "111", "Clean Code", "CC-1", "Código limpio"),
        book(2L, "222", "Refactoring", "RF-1", "Mejorar diseño")));

    assertAll(
        "Comprobar que dos libros se convierten manteniendo el orden",
        () -> assertEquals(List.of(
            new BookRecord(1L, "111", "Clean Code", "CC-1", "Código limpio"),
            new BookRecord(2L, "222", "Refactoring", "RF-1", "Mejorar diseño")), result)
    );
  }

  @Test
  void testBookModelToVoWithEmptyBook() {
    List<BookRecord> result = Mapper.bookModelToVo(List.of(new Book()));

    assertAll(
        "Comprobar que un libro sin datos se convierte en un record con todos los campos a null",
        () -> assertEquals(List.of(new BookRecord(null, null, null, null, null)), result)
    );
  }


  @Test
  void testAuthorModelToVoWithEmptyList() {
    List<AuthorRecord> result = Mapper.authorModelToVo(List.of());

    assertAll(
        "Comprobar que una lista vacía de autores se convierte en una lista vacía",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testAuthorModelToVoWithOneAuthor() {
    List<AuthorRecord> result = Mapper.authorModelToVo(List.of(author(1L, "Robert Martin", "Uncle Bob")));

    assertAll(
        "Comprobar que un autor se convierte copiando id, name y description",
        () -> assertEquals(List.of(new AuthorRecord(1L, "Robert Martin", "Uncle Bob")), result)
    );
  }

  @Test
  void testAuthorModelToVoWithTwoAuthors() {
    List<AuthorRecord> result = Mapper.authorModelToVo(List.of(
        author(1L, "Robert Martin", "Uncle Bob"),
        author(2L, "Martin Fowler", "Refactoring")));

    assertAll(
        "Comprobar que dos autores se convierten manteniendo el orden",
        () -> assertEquals(List.of(
            new AuthorRecord(1L, "Robert Martin", "Uncle Bob"),
            new AuthorRecord(2L, "Martin Fowler", "Refactoring")), result)
    );
  }

  @Test
  void testAuthorModelToVoWithEmptyAuthor() {
    List<AuthorRecord> result = Mapper.authorModelToVo(List.of(new Author()));

    assertAll(
        "Comprobar que un autor sin datos se convierte en un record con todos los campos a null",
        () -> assertEquals(List.of(new AuthorRecord(null, null, null)), result)
    );
  }


  @Test
  void testCategoryModelToVoWithEmptyList() {
    List<CategoryRecord> result = Mapper.categoryModelToVo(List.of());

    assertAll(
        "Comprobar que una lista vacía de categorías se convierte en una lista vacía",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testCategoryModelToVoWithOneCategory() {
    List<CategoryRecord> result = Mapper.categoryModelToVo(List.of(category(1L, "Software")));

    assertAll(
        "Comprobar que una categoría se convierte copiando id y name",
        () -> assertEquals(List.of(new CategoryRecord(1L, "Software")), result)
    );
  }

  @Test
  void testCategoryModelToVoWithTwoCategories() {
    List<CategoryRecord> result = Mapper.categoryModelToVo(List.of(
        category(1L, "Software"),
        category(2L, "Diseño")));

    assertAll(
        "Comprobar que dos categorías se convierten manteniendo el orden",
        () -> assertEquals(List.of(
            new CategoryRecord(1L, "Software"),
            new CategoryRecord(2L, "Diseño")), result)
    );
  }

  @Test
  void testCategoryModelToVoWithEmptyCategory() {
    List<CategoryRecord> result = Mapper.categoryModelToVo(List.of(new Category()));

    assertAll(
        "Comprobar que una categoría sin datos se convierte en un record con todos los campos a null",
        () -> assertEquals(List.of(new CategoryRecord(null, null)), result)
    );
  }


  @Test
  void testPublisherModelToVoWithEmptyList() {
    List<PublisherRecord> result = Mapper.publisherModelToVo(List.of());

    assertAll(
        "Comprobar que una lista vacía de editoriales se convierte en una lista vacía",
        () -> assertTrue(result.isEmpty())
    );
  }

  @Test
  void testPublisherModelToVoWithOnePublisher() {
    List<PublisherRecord> result = Mapper.publisherModelToVo(List.of(publisher(1L, "Prentice Hall")));

    assertAll(
        "Comprobar que una editorial se convierte copiando id y name",
        () -> assertEquals(List.of(new PublisherRecord(1L, "Prentice Hall")), result)
    );
  }

  @Test
  void testPublisherModelToVoWithTwoPublishers() {
    List<PublisherRecord> result = Mapper.publisherModelToVo(List.of(
        publisher(1L, "Prentice Hall"),
        publisher(2L, "Pearson")));

    assertAll(
        "Comprobar que dos editoriales se convierten manteniendo el orden",
        () -> assertEquals(List.of(
            new PublisherRecord(1L, "Prentice Hall"),
            new PublisherRecord(2L, "Pearson")), result)
    );
  }

  @Test
  void testPublisherModelToVoWithEmptyPublisher() {
    List<PublisherRecord> result = Mapper.publisherModelToVo(List.of(new Publisher()));

    assertAll(
        "Comprobar que una editorial sin datos se convierte en un record con todos los campos a null",
        () -> assertEquals(List.of(new PublisherRecord(null, null)), result)
    );
  }
}