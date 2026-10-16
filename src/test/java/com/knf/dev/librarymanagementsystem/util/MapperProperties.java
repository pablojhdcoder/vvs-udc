package com.knf.dev.librarymanagementsystem.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.vo.AuthorRecord;
import com.knf.dev.librarymanagementsystem.vo.BookRecord;
import com.knf.dev.librarymanagementsystem.vo.CategoryRecord;
import com.knf.dev.librarymanagementsystem.vo.PublisherRecord;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;


class MapperProperties {

  @Property(tries = 40)
  void bookModelToVoCopiaTodosLosCampos(
      @ForAll @Size(max = 20) List<@StringLength(max = 50) String> textos) {
    List<Book> books = new ArrayList<>();
    List<BookRecord> esperado = new ArrayList<>();
    for (int i = 0; i < textos.size(); i++) {
      String t = textos.get(i);
      Book book = new Book("isbn-" + t, t, "sn-" + t, "desc-" + t);
      book.setId((long) i);
      books.add(book);
      esperado.add(new BookRecord((long) i, "isbn-" + t, t, "sn-" + t, "desc-" + t));
    }

    List<BookRecord> result = Mapper.bookModelToVo(books);

    assertAll(
        "Comprobar que cada libro se convierte en su record, con los mismos campos y en el mismo orden",
        () -> assertEquals(esperado, result)
    );
  }

  @Property(tries = 40)
  void authorModelToVoCopiaTodosLosCampos(
      @ForAll @Size(max = 20) List<@StringLength(max = 50) String> textos) {
    List<Author> authors = new ArrayList<>();
    List<AuthorRecord> esperado = new ArrayList<>();
    for (int i = 0; i < textos.size(); i++) {
      String t = textos.get(i);
      Author author = new Author(t, "desc-" + t);
      author.setId((long) i);
      authors.add(author);
      esperado.add(new AuthorRecord((long) i, t, "desc-" + t));
    }

    List<AuthorRecord> result = Mapper.authorModelToVo(authors);

    assertAll(
        "Comprobar que cada autor se convierte en su record, con los mismos campos y en el mismo orden",
        () -> assertEquals(esperado, result)
    );
  }

  @Property(tries = 40)
  void categoryModelToVoCopiaTodosLosCampos(
      @ForAll @Size(max = 20) List<@StringLength(max = 50) String> nombres) {
    List<Category> categories = new ArrayList<>();
    List<CategoryRecord> esperado = new ArrayList<>();
    for (int i = 0; i < nombres.size(); i++) {
      Category category = new Category(nombres.get(i));
      category.setId((long) i);
      categories.add(category);
      esperado.add(new CategoryRecord((long) i, nombres.get(i)));
    }

    List<CategoryRecord> result = Mapper.categoryModelToVo(categories);

    assertAll(
        "Comprobar que cada categoría se convierte en su record, con los mismos campos y en el mismo orden",
        () -> assertEquals(esperado, result)
    );
  }

  @Property(tries = 40)
  void publisherModelToVoCopiaTodosLosCampos(
      @ForAll @Size(max = 20) List<@StringLength(max = 100) String> nombres) {
    List<Publisher> publishers = new ArrayList<>();
    List<PublisherRecord> esperado = new ArrayList<>();
    for (int i = 0; i < nombres.size(); i++) {
      Publisher publisher = new Publisher(nombres.get(i));
      publisher.setId((long) i);
      publishers.add(publisher);
      esperado.add(new PublisherRecord((long) i, nombres.get(i)));
    }

    List<PublisherRecord> result = Mapper.publisherModelToVo(publishers);

    assertAll(
        "Comprobar que cada editorial se convierte en su record, con los mismos campos y en el mismo orden",
        () -> assertEquals(esperado, result)
    );
  }
}