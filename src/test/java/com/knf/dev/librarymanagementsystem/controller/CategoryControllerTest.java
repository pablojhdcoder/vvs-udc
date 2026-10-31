package com.knf.dev.librarymanagementsystem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.service.CategoryService;

/**
 * CategoryController es un adaptador web fino: cada método público se prueba
 * con un mock del servicio. Un assert o un verify por método.
 */
class CategoryControllerTest {

	private CategoryService categoryService;
	private CategoryController controller;
	private Model model;

	@BeforeEach
	void setUp() {
		categoryService = mock(CategoryService.class);
		controller = new CategoryController(categoryService);
		model = mock(Model.class);
	}

	@Nested
	@DisplayName("findAllCategories")
	class FindAll {

		@Test
		@DisplayName("devuelve la vista list-categories")
		void vista() {
			assertEquals("list-categories", controller.findAllCategories(model));
		}

		@Test
		@DisplayName("delega en findAllCategories del servicio")
		void delega() {
			controller.findAllCategories(model);
			verify(categoryService).findAllCategories();
		}
	}

	@Nested
	@DisplayName("findCategoryById")
	class FindById {

		@Test
		@DisplayName("devuelve la vista list-category")
		void vista() {
			when(categoryService.findCategoryById(1L)).thenReturn(new Category("SciFi"));
			assertEquals("list-category", controller.findCategoryById(1L, model));
		}

		@Test
		@DisplayName("delega en findCategoryById")
		void delega() {
			when(categoryService.findCategoryById(7L)).thenReturn(new Category("X"));
			controller.findCategoryById(7L, model);
			verify(categoryService).findCategoryById(7L);
		}
	}

	@Nested
	@DisplayName("showCreateForm")
	class ShowCreate {

		@Test
		@DisplayName("devuelve add-category")
		void vista() {
			assertEquals("add-category", controller.showCreateForm(new Category()));
		}
	}

	@Nested
	@DisplayName("createCategory")
	class Create {

		@Test
		@DisplayName("sin errores redirige a /categories")
		void ok() {
			BindingResult result = mock(BindingResult.class);
			when(result.hasErrors()).thenReturn(false);
			assertEquals("redirect:/categories",
					controller.createCategory(new Category("A"), result, model));
		}

		@Test
		@DisplayName("con errores vuelve a add-category")
		void conErrores() {
			BindingResult result = mock(BindingResult.class);
			when(result.hasErrors()).thenReturn(true);
			assertEquals("add-category",
					controller.createCategory(new Category("A"), result, model));
		}

		@Test
		@DisplayName("sin errores delega en createCategory")
		void delega() {
			BindingResult result = mock(BindingResult.class);
			when(result.hasErrors()).thenReturn(false);
			Category category = new Category("A");
			controller.createCategory(category, result, model);
			verify(categoryService).createCategory(category);
		}
	}

	@Nested
	@DisplayName("showUpdateForm")
	class ShowUpdate {

		@Test
		@DisplayName("devuelve update-category")
		void vista() {
			when(categoryService.findCategoryById(1L)).thenReturn(new Category("A"));
			assertEquals("update-category", controller.showUpdateForm(1L, model));
		}
	}

	@Nested
	@DisplayName("updateCategory")
	class Update {

		@Test
		@DisplayName("sin errores redirige a /categories")
		void ok() {
			BindingResult result = mock(BindingResult.class);
			when(result.hasErrors()).thenReturn(false);
			assertEquals("redirect:/categories",
					controller.updateCategory(1L, new Category("A"), result, model));
		}

		@Test
		@DisplayName("con errores vuelve a update-category")
		void conErrores() {
			BindingResult result = mock(BindingResult.class);
			when(result.hasErrors()).thenReturn(true);
			assertEquals("update-category",
					controller.updateCategory(1L, new Category("A"), result, model));
		}
	}

	@Nested
	@DisplayName("deleteCategory")
	class Delete {

		@Test
		@DisplayName("redirige a /categories")
		void vista() {
			assertEquals("redirect:/categories", controller.deleteCategory(1L, model));
		}

		@Test
		@DisplayName("delega en deleteCategory")
		void delega() {
			controller.deleteCategory(3L, model);
			verify(categoryService).deleteCategory(3L);
		}
	}
}
