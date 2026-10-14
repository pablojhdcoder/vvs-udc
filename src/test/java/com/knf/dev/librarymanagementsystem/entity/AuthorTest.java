package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Author no tiene add/remove: el set de libros cambia con setBooks o mutando
 * lo que devuelve getBooks.
 *
 * <pre>
 *   [Vacio] --add(nuevo)--> [ConElementos]
 *   [ConElementos] --add(el mismo)--> [ConElementos]
 *   [ConElementos] --add(otro)--> [ConElementos]
 *   [ConElementos] --remove(el último)--> [Vacio]
 *   [ConElementos] --remove(uno de varios)--> [ConElementos]
 *   [Vacio] --remove--> [Vacio]
 *   [Vacio] --set(no vacío)--> [ConElementos]
 *   [ConElementos] --set(vacío)--> [Vacio]
 * </pre>
 *
 * Cada transición es un método. Un assert por método.
 * name tiene length 100 y description length 250: vacío, un carácter, el máximo y uno más.
 */
class AuthorTest {

	private static final String NAME = "Pablo";
	private static final String DESCRIPTION = "Maravilloso";

	private Author autor() {
		return new Author(NAME, DESCRIPTION);
	}

	private Book libro(String isbn) {
		return new Book(isbn, "CaperucitaRoja", "CC-1", "desc");
	}

	static Stream<String> textosFronteraNombre() {
		return Stream.of("a", "", "a".repeat(100), "a".repeat(101));
	}

	static Stream<String> textosFronteraDescripcion() {
		return Stream.of("a", "", "a".repeat(250), "a".repeat(251));
	}

	@Nested
	@DisplayName("Constructor vacío")
	class ConstructorVacio {

		@Test
		@DisplayName("id queda null")
		void idNull() {
			assertNull(new Author().getId());
		}

		@Test
		@DisplayName("name queda null")
		void nameNull() {
			assertNull(new Author().getName());
		}

		@Test
		@DisplayName("description queda null")
		void descriptionNull() {
			assertNull(new Author().getDescription());
		}

		@Test
		@DisplayName("books empieza vacío")
		void booksVacio() {
			assertTrue(new Author().getBooks().isEmpty());
		}
	}

	@Nested
	@DisplayName("Constructor con nombre y descripción")
	class ConstructorConArgumentos {

		@Test
		@DisplayName("guarda el nombre")
		void guardaNombre() {
			assertEquals(NAME, autor().getName());
		}

		@Test
		@DisplayName("guarda la descripción")
		void guardaDescripcion() {
			assertEquals(DESCRIPTION, autor().getDescription());
		}

		@Test
		@DisplayName("el id sigue null hasta que lo asigne la persistencia")
		void idSigueNull() {
			assertNull(autor().getId());
		}

		@Test
		@DisplayName("books sigue vacío")
		void booksVacio() {
			assertTrue(autor().getBooks().isEmpty());
		}

		@ParameterizedTest
		@MethodSource("com.knf.dev.librarymanagementsystem.entity.AuthorTest#textosFronteraNombre")
		@DisplayName("name: valor corto, vacío, longitud 100 y 101")
		void nombreEnFrontera(String name) {
			Author author = new Author(name, DESCRIPTION);

			assertEquals(name, author.getName());
		}

		@ParameterizedTest
		@MethodSource("com.knf.dev.librarymanagementsystem.entity.AuthorTest#textosFronteraDescripcion")
		@DisplayName("description: valor corto, vacío, longitud 250 y 251")
		void descripcionEnFrontera(String description) {
			Author author = new Author(NAME, description);

			assertEquals(description, author.getDescription());
		}
	}

	@Nested
	@DisplayName("Setters")
	class Setters {

		@ParameterizedTest
		@ValueSource(longs = { 1L, 0L, Long.MAX_VALUE })
		@DisplayName("setId conserva 1, 0 y el máximo long")
		void setId(long id) {
			Author author = new Author();
			author.setId(id);

			assertEquals(id, author.getId());
		}

		@Test
		@DisplayName("setId acepta null")
		void setIdNull() {
			Author author = autor();
			author.setId(7L);
			author.setId(null);

			assertNull(author.getId());
		}

		@ParameterizedTest
		@NullSource
		@MethodSource("com.knf.dev.librarymanagementsystem.entity.AuthorTest#textosFronteraNombre")
		@DisplayName("setName: null, corto, vacío, 100 y 101")
		void setName(String name) {
			Author author = autor();
			author.setName(name);

			assertEquals(name, author.getName());
		}

		@ParameterizedTest
		@NullSource
		@MethodSource("com.knf.dev.librarymanagementsystem.entity.AuthorTest#textosFronteraDescripcion")
		@DisplayName("setDescription: null, corto, vacío, 250 y 251")
		void setDescription(String description) {
			Author author = autor();
			author.setDescription(description);

			assertEquals(description, author.getDescription());
		}

		@Test
		@DisplayName("setBooks sustituye la colección")
		void setBooksSustituye() {
			Author author = autor();
			Set<Book> libros = new HashSet<>();
			libros.add(libro("9788426146045"));

			author.setBooks(libros);

			assertSame(libros, author.getBooks());
		}
	}

	@Nested
	@DisplayName("Libros: transiciones del set")
	class Libros {

		@Test
		@DisplayName("Vacío --add--> ConElementos")
		void addPasaAConElementos() {
			Author author = autor();
			Book book = libro("978-1");

			author.getBooks().add(book);

			assertTrue(author.getBooks().contains(book));
		}

		@Test
		@DisplayName("add del mismo libro no duplica")
		void addRepetidoNoDuplica() {
			Author author = autor();
			Book book = libro("978-1");
			author.getBooks().add(book);

			author.getBooks().add(book);

			assertEquals(1, author.getBooks().size());
		}

		@Test
		@DisplayName("add de otro libro deja dos")
		void addDeOtroLibro() {
			Author author = autor();
			author.getBooks().add(libro("978-1"));

			author.getBooks().add(libro("978-2"));

			assertEquals(2, author.getBooks().size());
		}

		@Test
		@DisplayName("ConElementos --remove(el último)--> Vacío")
		void removeUltimoDejaVacio() {
			Author author = autor();
			Book book = libro("978-1");
			author.getBooks().add(book);

			author.getBooks().remove(book);

			assertTrue(author.getBooks().isEmpty());
		}

		@Test
		@DisplayName("remove de uno de varios conserva el otro")
		void removeUnoDeVariosConservaElOtro() {
			Author author = autor();
			Book primero = libro("978-1");
			Book segundo = libro("978-2");
			author.getBooks().add(primero);
			author.getBooks().add(segundo);

			author.getBooks().remove(primero);

			assertTrue(author.getBooks().contains(segundo));
		}

		@Test
		@DisplayName("Vacío --remove--> Vacío")
		void removeEnVacioSigueVacio() {
			Author author = autor();

			author.getBooks().remove(libro("978-1"));

			assertTrue(author.getBooks().isEmpty());
		}

		@Test
		@DisplayName("Vacío --set(no vacío)--> ConElementos")
		void setBooksLlena() {
			Author author = autor();
			Book book = libro("978-1");
			Set<Book> libros = new HashSet<>();
			libros.add(book);

			author.setBooks(libros);

			assertTrue(author.getBooks().contains(book));
		}

		@Test
		@DisplayName("ConElementos --set(vacío)--> Vacío")
		void setBooksVacio() {
			Author author = autor();
			author.getBooks().add(libro("978-1"));

			author.setBooks(new HashSet<>());

			assertTrue(author.getBooks().isEmpty());
		}
	}

	@Nested
	@DisplayName("El getter devuelve el set interno")
	class SetInterno {

		@Test
		@DisplayName("clear sobre el set devuelto deja al autor sin libros")
		void clearSobreElSetDevuelto() {
			Author author = autor();
			author.getBooks().add(libro("978-1"));

			author.getBooks().clear();

			assertTrue(author.getBooks().isEmpty());
		}

		@Test
		@DisplayName("mutar el set original después de setBooks cambia el autor")
		void setBooksGuardaLaMismaReferencia() {
			Author author = autor();
			Set<Book> libros = new HashSet<>();
			author.setBooks(libros);

			libros.add(libro("978-1"));

			assertEquals(1, author.getBooks().size());
		}
	}
}
