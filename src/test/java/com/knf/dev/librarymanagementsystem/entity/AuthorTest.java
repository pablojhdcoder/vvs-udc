package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthorTest {

	private Author createAuthor(String name, String description) {
		return new Author(name, description);
	}

	@Nested
	@DisplayName("Constructores")
	class Constructores {

		@Test
		@DisplayName("el constructor con nombre y descripción los rellena, id nulo y books vacío")
		void constructorConNombreYDescripcion() {
			Author author = createAuthor("Pablo", "Maravilloso");

			assertEquals("Pablo", author.getName());
			assertEquals("Maravilloso", author.getDescription());
			assertNull(author.getId());
			assertTrue(author.getBooks().isEmpty());
		}

		@Test
		@DisplayName("el constructor vacío deja id, name y description a null y books vacío")
		void constructorVacio() {
			Author author = new Author();

			assertNull(author.getName());
			assertNull(author.getDescription());
			assertNull(author.getId());
			assertTrue(author.getBooks().isEmpty());
		}
	}

	@Nested
	@DisplayName("Getters y setters")
	class Accesores {

		@Test
		@DisplayName("setId, setName y setDescription actualizan los campos")
		void settersSimples() {
			Author author = new Author();

			author.setId(1L);
			author.setName("Pablo");
			author.setDescription("Maravilloso");

			assertEquals(1L, author.getId());
			assertEquals("Pablo", author.getName());
			assertEquals("Maravilloso", author.getDescription());
		}

		@Test
		@DisplayName("setBooks sustituye el set de libros")
		void setBooksSustituyeElSet() {
			Author author = createAuthor("Pablo", "Maravilloso");
			Set<Book> libros = new HashSet<>();
			Book book = new Book("9788426146045", "CaperucitaRoja", "CC-1", "desc");
			libros.add(book);

			author.setBooks(libros);

			assertSame(libros, author.getBooks());
			assertTrue(author.getBooks().contains(book));
		}
	}
}
