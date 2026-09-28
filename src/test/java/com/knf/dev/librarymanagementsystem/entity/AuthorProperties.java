package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
