package com.knf.dev.librarymanagementsystem.entity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PublisherTest {
    @Nested
	@DisplayName("Constructores")
    class Constructores {

        @Test
		@DisplayName("el constructor vacío deja id y name a null y books vacío")
		void constructorVacio() {
			Publisher publisher = new Publisher();

			assertNull(publisher.getId());
			assertNull(publisher.getName());
			assertTrue(publisher.getBooks().isEmpty());
		}

        @Test
		@DisplayName("el constructor con nombre rellena name y deja id a null")
		void constructorConNombre() {
			Publisher publisher = new Publisher("Prentice Hall");

			assertEquals("Prentice Hall", publisher.getName());
			assertNull(publisher.getId());
			assertTrue(publisher.getBooks().isEmpty());
		}
    }

    @Nested
	@DisplayName("Getters y setters")
    class Accesores {

        @Test
		@DisplayName("setId y setName actualizan los campos")
		void settersSimples() {
			Publisher publisher = new Publisher();

			publisher.setId(3L);
			publisher.setName("Pearson");

			assertEquals(3L, publisher.getId());
			assertEquals("Pearson", publisher.getName());
		}

        @Test
		@DisplayName("setBooks sustituye el set de libros")
		void setBooksSustituyeElSet() {
			Publisher publisher = new Publisher("Pearson");
			Set<Book> libros = new HashSet<>();
			Book book = new Book("978-1", "Clean Code", "CC-1", "desc");
			libros.add(book);

			publisher.setBooks(libros);

			assertSame(libros, publisher.getBooks());
			assertTrue(publisher.getBooks().contains(book));
		}

    }
}
