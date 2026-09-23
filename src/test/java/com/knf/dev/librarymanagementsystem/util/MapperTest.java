package com.knf.dev.librarymanagementsystem.util;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

	@Nested
	@DisplayName("bookModelToVo")
	class Libros {

		@Test
		@DisplayName("lista vacía devuelve lista vacía")
		void listaVacia() {
			assertTrue(Mapper.bookModelToVo(List.of()).isEmpty());
		}

		@Test
		@DisplayName("copia id, isbn, name, serialName y description en orden")
		void conElementos() {
			Book b1 = new Book("111", "Clean Code", "CC-1", "Código limpio");
			b1.setId(1L);
			Book b2 = new Book("222", "Refactoring", "RF-1", "Mejorar diseño");
			b2.setId(2L);

			List<BookRecord> resultado = Mapper.bookModelToVo(List.of(b1, b2));

			assertEquals(List.of(
					new BookRecord(1L, "111", "Clean Code", "CC-1", "Código limpio"),
					new BookRecord(2L, "222", "Refactoring", "RF-1", "Mejorar diseño")),
					resultado);
		}
	}

	@Nested
	@DisplayName("authorModelToVo")
	class Autores {

		@Test
		@DisplayName("lista vacía devuelve lista vacía")
		void listaVacia() {
			assertTrue(Mapper.authorModelToVo(List.of()).isEmpty());
		}

		@Test
		@DisplayName("copia id, name y description en orden")
		void conElementos() {
			Author a1 = new Author("Robert Martin", "Uncle Bob");
			a1.setId(1L);
			Author a2 = new Author("Martin Fowler", "Refactoring");
			a2.setId(2L);

			List<AuthorRecord> resultado = Mapper.authorModelToVo(List.of(a1, a2));

			assertEquals(List.of(
					new AuthorRecord(1L, "Robert Martin", "Uncle Bob"),
					new AuthorRecord(2L, "Martin Fowler", "Refactoring")),
					resultado);
		}
	}

	@Nested
	@DisplayName("categoryModelToVo")
	class Categorias {

		@Test
		@DisplayName("lista vacía devuelve lista vacía")
		void listaVacia() {
			assertTrue(Mapper.categoryModelToVo(List.of()).isEmpty());
		}

		@Test
		@DisplayName("copia id y name en orden")
		void conElementos() {
			Category c1 = new Category("Software");
			c1.setId(1L);
			Category c2 = new Category("Diseño");
			c2.setId(2L);

			List<CategoryRecord> resultado = Mapper.categoryModelToVo(List.of(c1, c2));

			assertEquals(List.of(
					new CategoryRecord(1L, "Software"),
					new CategoryRecord(2L, "Diseño")),
					resultado);
		}
	}

	@Nested
	@DisplayName("publisherModelToVo")
	class Editoriales {

		@Test
		@DisplayName("lista vacía devuelve lista vacía")
		void listaVacia() {
			assertTrue(Mapper.publisherModelToVo(List.of()).isEmpty());
		}

		@Test
		@DisplayName("copia id y name en orden")
		void conElementos() {
			Publisher p1 = new Publisher("Prentice Hall");
			p1.setId(1L);
			Publisher p2 = new Publisher("Pearson");
			p2.setId(2L);

			List<PublisherRecord> resultado = Mapper.publisherModelToVo(List.of(p1, p2));

			assertEquals(List.of(
					new PublisherRecord(1L, "Prentice Hall"),
					new PublisherRecord(2L, "Pearson")),
					resultado);
		}
	}
}
