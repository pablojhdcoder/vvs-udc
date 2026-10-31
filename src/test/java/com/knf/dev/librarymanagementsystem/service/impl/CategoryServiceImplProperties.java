package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.support.InMemoryCategoryCatalog;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;

/**
 * Datos aleatorios sobre CategoryServiceImpl. Un assert o un verify por propiedad.
 */
class CategoryServiceImplProperties {

	@Property(tries = 20)
	void findCategoryByIdInexistenteMencionaElId(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());
		String mensaje = null;
		try {
			service.findCategoryById(id);
		} catch (NotFoundException ex) {
			mensaje = ex.getMessage();
		}

		assertEquals("Category not found  with ID " + id, mensaje);
	}

	@Property(tries = 15)
	void findCategoryByIdInexistenteLanza(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.findCategoryById(id));
	}

	@Property(tries = 20)
	void createSiempreHaceSave(@ForAll @StringLength(min = 1, max = 50) String name) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		Category category = new Category(name);

		service.createCategory(category);

		verify(repository).save(category);
	}

	@Property(tries = 20)
	void updateSiempreHaceSave(@ForAll @StringLength(min = 1, max = 50) String name) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		Category category = new Category(name);

		service.updateCategory(category);

		verify(repository).save(category);
	}

	@Property(tries = 15)
	void deleteExistenteBorraPorSuId(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		Category category = new Category("Ficción");
		category.setId(id);
		when(repository.findById(id)).thenReturn(Optional.of(category));

		service.deleteCategory(id);

		verify(repository).deleteById(id);
	}

	@Property(tries = 15)
	void deleteInexistenteLanza(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.deleteCategory(id));
	}

	@Property(tries = 15)
	void deleteInexistenteNoBorra(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		CategoryRepository repository = mock(CategoryRepository.class);
		CategoryServiceImpl service = new CategoryServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		try {
			service.deleteCategory(id);
		} catch (NotFoundException ex) {
			// la comprobación es que no se borra
		}

		verify(repository, never()).deleteById(anyLong());
	}

	@Property(tries = 15)
	void trasCrearFindByIdDevuelveElNombre(@ForAll @StringLength(min = 1, max = 50) String name) {
		CategoryServiceImpl service = new InMemoryCategoryCatalog().service();
		Category category = new Category(name);

		service.createCategory(category);

		assertEquals(name, service.findCategoryById(category.getId()).getName());
	}

	@Property(tries = 15)
	void trasActualizarElNombreEsElNuevo(@ForAll @StringLength(min = 1, max = 40) String name) {
		CategoryServiceImpl service = new InMemoryCategoryCatalog().service();
		Category category = new Category(name);
		service.createCategory(category);
		Category stored = service.findCategoryById(category.getId());
		stored.setName(name + "x");

		service.updateCategory(stored);

		assertEquals(name + "x", service.findCategoryById(category.getId()).getName());
	}

	@Property(tries = 15)
	void trasBorrarFindAllEstaVacio(@ForAll @StringLength(min = 1, max = 50) String name) {
		CategoryServiceImpl service = new InMemoryCategoryCatalog().service();
		Category category = new Category(name);
		service.createCategory(category);

		service.deleteCategory(category.getId());

		assertTrue(service.findAllCategories().isEmpty());
	}

	@Property(tries = 15)
	void trasBorrarFindByIdLanza(@ForAll @StringLength(min = 1, max = 50) String name) {
		CategoryServiceImpl service = new InMemoryCategoryCatalog().service();
		Category category = new Category(name);
		service.createCategory(category);
		Long id = category.getId();

		service.deleteCategory(id);

		assertThrows(NotFoundException.class, () -> service.findCategoryById(id));
	}
}
