package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.StringLength;

/**
 * Datos aleatorios de Author. Las fronteras de columna están en AuthorTest.
 * Aquí el azar se queda dentro de length (name 100, description 250). Un assert por propiedad.
 */
class AuthorProperties {

	@Property(tries = 40)
	void constructorConservaNombre(@ForAll @StringLength(max = 100) String name) {
		Author author = new Author(name, "desc");

		assertEquals(name, author.getName());
	}

	@Property(tries = 40)
	void constructorConservaDescripcion(@ForAll @StringLength(max = 250) String description) {
		Author author = new Author("nombre", description);

		assertEquals(description, author.getDescription());
	}

	@Property(tries = 30)
	void constructorDejaBooksVacio(
			@ForAll @StringLength(max = 20) String name,
			@ForAll @StringLength(max = 20) String description) {
		Author author = new Author(name, description);

		assertTrue(author.getBooks().isEmpty());
	}

	@Property(tries = 30)
	void setNameConservaElValor(@ForAll @StringLength(max = 100) String name) {
		Author author = new Author();
		author.setName(name);

		assertEquals(name, author.getName());
	}

	@Property(tries = 30)
	void setDescriptionConservaElValor(@ForAll @StringLength(max = 250) String description) {
		Author author = new Author();
		author.setDescription(description);

		assertEquals(description, author.getDescription());
	}

	@Property(tries = 20)
	void anadirElMismoLibroNoDuplica(@ForAll @StringLength(min = 1, max = 30) String nombreLibro) {
		Author author = new Author("Ada", "desc");
		Book book = new Book("isbn-" + nombreLibro, nombreLibro, "SN", "d");
		author.getBooks().add(book);

		author.getBooks().add(book);

		assertEquals(1, author.getBooks().size());
	}

	@Property(tries = 20)
	void trasAnadirUnLibroElTamanoEsUno(@ForAll @StringLength(min = 1, max = 40) String name) {
		Author author = new Author(name, "desc");

		author.getBooks().add(new Book("isbn-" + name, name, "SN", "d"));

		assertEquals(1, author.getBooks().size());
	}

	@Property(tries = 20)
	void setBooksVacioDejaLaColeccionVacia(@ForAll @StringLength(min = 1, max = 40) String name) {
		Author author = new Author(name, "desc");
		author.getBooks().add(new Book("isbn-" + name, name, "SN", "d"));

		author.setBooks(new HashSet<>());

		assertTrue(author.getBooks().isEmpty());
	}
}
