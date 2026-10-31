package com.knf.dev.librarymanagementsystem.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.repository.CategoryRepository;
import com.knf.dev.librarymanagementsystem.service.impl.CategoryServiceImpl;

/**
 * Doble de {@link CategoryRepository} que guarda categorías en memoria.
 */
public final class InMemoryCategoryCatalog {

	private final List<Category> categories = new ArrayList<>();
	private final AtomicLong nextId = new AtomicLong(1);
	private final CategoryRepository repository;
	private final CategoryServiceImpl service;

	public InMemoryCategoryCatalog() {
		repository = mock(CategoryRepository.class);
		service = new CategoryServiceImpl(repository);
		when(repository.findAll()).thenAnswer(invocation -> new ArrayList<>(categories));
		when(repository.findById(anyLong())).thenAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			return categories.stream().filter(category -> id.equals(category.getId())).findFirst();
		});
		when(repository.save(any(Category.class))).thenAnswer(invocation -> {
			Category category = invocation.getArgument(0);
			if (category.getId() == null) {
				category.setId(nextId.getAndIncrement());
			}
			categories.removeIf(existing -> category.getId().equals(existing.getId()));
			categories.add(category);
			return category;
		});
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			categories.removeIf(category -> id.equals(category.getId()));
			return null;
		}).when(repository).deleteById(anyLong());
	}

	public CategoryServiceImpl service() {
		return service;
	}

	public CategoryRepository repository() {
		return repository;
	}

	public int size() {
		return categories.size();
	}
}
