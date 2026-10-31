package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.service.CategoryService;

@SpringBootTest
@Transactional
class CategoryServiceIntegrationTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BookRepository bookRepository;

	@BeforeEach
	void setUp() {
		bookRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Nested
	@DisplayName("findAllCategories")
	class FindAll {

		@Test
		@DisplayName("con dos categorías: tamaño 2")
		void tamanoDos() {
			categoryRepository.save(new Category("Category 1"));
			categoryRepository.save(new Category("Category 2"));

			assertEquals(2, categoryService.findAllCategories().size());
		}

		@Test
		@DisplayName("con dos categorías: primera es Category 1")
		void primeraNombre() {
			categoryRepository.save(new Category("Category 1"));
			categoryRepository.save(new Category("Category 2"));
			List<Category> result = categoryService.findAllCategories();

			assertEquals("Category 1", result.get(0).getName());
		}

		@Test
		@DisplayName("con dos categorías: segunda es Category 2")
		void segundaNombre() {
			categoryRepository.save(new Category("Category 1"));
			categoryRepository.save(new Category("Category 2"));
			List<Category> result = categoryService.findAllCategories();

			assertEquals("Category 2", result.get(1).getName());
		}
	}

	@Nested
	@DisplayName("findCategoryById")
	class FindById {

		@Test
		@DisplayName("categoría existente no es null")
		void noNull() {
			Category saved = categoryRepository.save(new Category("Category 1"));

			assertNotNull(categoryService.findCategoryById(saved.getId()));
		}

		@Test
		@DisplayName("devuelve el mismo id")
		void mismoId() {
			Category saved = categoryRepository.save(new Category("Category 1"));

			assertEquals(saved.getId(), categoryService.findCategoryById(saved.getId()).getId());
		}

		@Test
		@DisplayName("devuelve el mismo nombre")
		void mismoNombre() {
			Category saved = categoryRepository.save(new Category("Category 1"));

			assertEquals("Category 1", categoryService.findCategoryById(saved.getId()).getName());
		}

		@Test
		@DisplayName("id inexistente lanza NotFoundException")
		void inexistente() {
			assertThrows(NotFoundException.class, () -> categoryService.findCategoryById(999L));
		}
	}

	@Nested
	@DisplayName("createCategory")
	class Create {

		@Test
		@DisplayName("tras crear hay una categoría")
		void tamanoUno() {
			categoryService.createCategory(new Category("Category 1"));

			assertEquals(1, categoryRepository.findAll().size());
		}

		@Test
		@DisplayName("tras crear el nombre queda guardado")
		void guardaNombre() {
			categoryService.createCategory(new Category("Category 1"));

			assertEquals("Category 1", categoryRepository.findAll().get(0).getName());
		}

		@Test
		@DisplayName("tras crear la base asigna un id")
		void asignaId() {
			categoryService.createCategory(new Category("Category 1"));

			assertNotNull(categoryRepository.findAll().get(0).getId());
		}
	}

	@Nested
	@DisplayName("updateCategory")
	class Update {

		@Test
		@DisplayName("conserva el id")
		void conservaId() {
			Category saved = categoryRepository.save(new Category("Category 1"));
			saved.setName("Updated Category");
			categoryService.updateCategory(saved);
			Category result = categoryRepository.findById(saved.getId()).orElseThrow();

			assertEquals(saved.getId(), result.getId());
		}

		@Test
		@DisplayName("cambia el nombre")
		void cambiaNombre() {
			Category saved = categoryRepository.save(new Category("Category 1"));
			saved.setName("Updated Category");
			categoryService.updateCategory(saved);
			Category result = categoryRepository.findById(saved.getId()).orElseThrow();

			assertEquals("Updated Category", result.getName());
		}
	}

	@Nested
	@DisplayName("deleteCategory")
	class Delete {

		@Test
		@DisplayName("borra la categoría")
		void borra() {
			Category saved = categoryRepository.save(new Category("Category 1"));

			categoryService.deleteCategory(saved.getId());

			assertTrue(categoryRepository.findById(saved.getId()).isEmpty());
		}

		@Test
		@DisplayName("entre varias solo borra la pedida")
		void borraSoloLaPedida() {
			Category saved1 = categoryRepository.save(new Category("Category 1"));
			categoryRepository.save(new Category("Category 2"));

			categoryService.deleteCategory(saved1.getId());

			assertTrue(categoryRepository.findById(saved1.getId()).isEmpty());
		}

		@Test
		@DisplayName("entre varias la otra sigue existiendo")
		void laOtraSigue() {
			categoryRepository.save(new Category("Category 1"));
			Category saved2 = categoryRepository.save(new Category("Category 2"));

			categoryService.deleteCategory(categoryRepository.findAll().get(0).getId());

			assertEquals(saved2, categoryRepository.findById(saved2.getId()).orElseThrow());
		}

		@Test
		@DisplayName("id inexistente lanza NotFoundException")
		void inexistente() {
			assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(999L));
		}
	}
}
