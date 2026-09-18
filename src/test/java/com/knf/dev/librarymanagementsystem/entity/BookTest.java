package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BookTest {

	private Book libro(String isbn) {
		return new Book(isbn, "Clean Code", "CC-1", "Guía de código limpio");
	}

	@Nested
	@DisplayName("Constructores y getters/setters")
	class ConstructoresYAccesores {

		@Test
		@DisplayName("el constructor vacío deja los campos a null y las colecciones vacías")
		void constructorVacio() {
			Book book = new Book();

			assertEquals(null, book.getId());
			assertEquals(null, book.getIsbn());
			assertEquals(null, book.getName());
			assertEquals(null, book.getSerialName());
			assertEquals(null, book.getDescription());
			assertTrue(book.getAuthors().isEmpty());
			assertTrue(book.getCategories().isEmpty());
			assertTrue(book.getPublishers().isEmpty());
		}

		@Test
		@DisplayName("el constructor con argumentos rellena isbn, name, serialName y description")
		void constructorConArgumentos() {
			Book book = libro("978-0132350884");

			assertEquals("978-0132350884", book.getIsbn());
			assertEquals("Clean Code", book.getName());
			assertEquals("CC-1", book.getSerialName());
			assertEquals("Guía de código limpio", book.getDescription());
			assertEquals(null, book.getId());
		}

		@Test
		@DisplayName("los setters actualizan id y el resto de campos simples")
		void settersYGetters() {
			Book book = new Book();

			book.setId(7L);
			book.setIsbn("111");
			book.setName("Refactoring");
			book.setSerialName("RF-1");
			book.setDescription("Mejorar diseño");

			assertEquals(7L, book.getId());
			assertEquals("111", book.getIsbn());
			assertEquals("Refactoring", book.getName());
			assertEquals("RF-1", book.getSerialName());
			assertEquals("Mejorar diseño", book.getDescription());
		}
	}

	@Nested
	@DisplayName("Relación bidireccional con autores")
	class Autores {

		@Test
		@DisplayName("addAuthors mete el autor en el libro y el libro en el autor")
		void addAuthorsEnlazaLosDosLados() {
			Book book = libro("978-1");
			Author author = new Author("Robert Martin", "Uncle Bob");

			book.addAuthors(author);

			assertTrue(book.getAuthors().contains(author));
			assertTrue(author.getBooks().contains(book));
			assertEquals(1, book.getAuthors().size());
		}

		@Test
		@DisplayName("removeAuthors quita el autor del libro y el libro del autor")
		void removeAuthorsRompeLosDosLados() {
			Book book = libro("978-1");
			Author author = new Author("Robert Martin", "Uncle Bob");
			book.addAuthors(author);

			book.removeAuthors(author);

			assertTrue(book.getAuthors().isEmpty());
			assertTrue(author.getBooks().isEmpty());
		}
	}

	@Nested
	@DisplayName("Relación bidireccional con categorías")
	class Categorias {

		@Test
		@DisplayName("addCategories mete la categoría en el libro y el libro en la categoría")
		void addCategoriesEnlazaLosDosLados() {
			Book book = libro("978-1");
			Category category = new Category("Software");

			book.addCategories(category);

			assertTrue(book.getCategories().contains(category));
			assertTrue(category.getBooks().contains(book));
		}

		@Test
		@DisplayName("removeCategories quita la categoría de ambos lados")
		void removeCategoriesRompeLosDosLados() {
			Book book = libro("978-1");
			Category category = new Category("Software");
			book.addCategories(category);

			book.removeCategories(category);

			assertTrue(book.getCategories().isEmpty());
			assertTrue(category.getBooks().isEmpty());
		}
	}

	@Nested
	@DisplayName("Relación bidireccional con editoriales")
	class Editoriales {

		@Test
		@DisplayName("addPublishers mete la editorial en el libro y el libro en la editorial")
		void addPublishersEnlazaLosDosLados() {
			Book book = libro("978-1");
			Publisher publisher = new Publisher("Prentice Hall");

			book.addPublishers(publisher);

			assertTrue(book.getPublishers().contains(publisher));
			assertTrue(publisher.getBooks().contains(book));
		}

		@Test
		@DisplayName("removePublishers quita la editorial de ambos lados")
		void removePublishersRompeLosDosLados() {
			Book book = libro("978-1");
			Publisher publisher = new Publisher("Prentice Hall");
			book.addPublishers(publisher);

			book.removePublishers(publisher);

			assertTrue(book.getPublishers().isEmpty());
			assertTrue(publisher.getBooks().isEmpty());
		}
	}
}
