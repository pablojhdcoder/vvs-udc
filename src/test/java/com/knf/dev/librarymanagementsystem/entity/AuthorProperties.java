package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;

class AuthorProperties {

	@Property(tries = 50)
	void constructorPreservesNameAndDescription(
			@ForAll @StringLength(max = 100) String name,
			@ForAll @StringLength(max = 250) String description) {
		Author author = new Author(name, description);

		assertEquals(name, author.getName());
		assertEquals(description, author.getDescription());
		assertTrue(author.getBooks().isEmpty());
	}

	@Property(tries = 50)
	void settersPreserveAssignedValues(
			@ForAll @StringLength(max = 100) String name,
			@ForAll @StringLength(max = 250) String description) {
		Author author = new Author();

		author.setName(name);
		author.setDescription(description);

		assertEquals(name, author.getName());
		assertEquals(description, author.getDescription());
	}

	@Property(tries = 30)
	void booksVaciosLuegoConLibroLuegoVacios(@ForAll @StringLength(min = 1, max = 40) String name) {
		Author author = new Author(name, "desc");
		assertTrue(author.getBooks().isEmpty());

		author.getBooks().add(new Book("isbn-" + name, name, "SN", "d"));
		assertEquals(1, author.getBooks().size());

		author.setBooks(new HashSet<>());
		assertTrue(author.getBooks().isEmpty());
	}
}
