package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;

/**
 * Datos aleatorios (jqwik) para as operacións públicas de Book. Un assert por propiedade.
 */
class BookProperties {

	@Property(tries = 40)
	void constructorConservaIsbn(@ForAll @StringLength(max = 50) String isbn) {
		Book book = new Book(isbn, "nome", "serial", "desc");

		assertEquals(isbn, book.getIsbn());
	}

	@Property(tries = 40)
	void constructorConservaNome(@ForAll @StringLength(max = 100) String name) {
		Book book = new Book("isbn", name, "serial", "desc");

		assertEquals(name, book.getName());
	}

	@Property(tries = 40)
	void constructorConservaSerial(@ForAll @StringLength(max = 50) String serial) {
		Book book = new Book("isbn", "nome", serial, "desc");

		assertEquals(serial, book.getSerialName());
	}

	@Property(tries = 40)
	void constructorConservaDescricion(@ForAll @StringLength(max = 250) String description) {
		Book book = new Book("isbn", "nome", "serial", description);

		assertEquals(description, book.getDescription());
	}

	@Property(tries = 30)
	void constructorDeixaAuthorsBaleiro(
			@ForAll @StringLength(max = 20) String isbn,
			@ForAll @StringLength(max = 20) String name) {
		Book book = new Book(isbn, name, "serial", "desc");

		assertTrue(book.getAuthors().isEmpty());
	}

	@Property(tries = 30)
	void setIsbnConservaOValor(@ForAll @StringLength(max = 50) String isbn) {
		Book book = new Book();
		book.setIsbn(isbn);

		assertEquals(isbn, book.getIsbn());
	}

	@Property(tries = 30)
	void setNameConservaOValor(@ForAll @StringLength(max = 100) String name) {
		Book book = new Book();
		book.setName(name);

		assertEquals(name, book.getName());
	}

	@Property(tries = 20)
	void addAuthorsEIdempotenteNoTamano(@ForAll @StringLength(min = 1, max = 30) String nomeAutor) {
		Book book = new Book("isbn", "nome", "serial", "desc");
		Author author = new Author(nomeAutor, "desc");
		book.addAuthors(author);

		book.addAuthors(author);

		assertEquals(1, book.getAuthors().size());
	}
}
