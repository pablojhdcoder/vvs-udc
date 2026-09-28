package com.knf.dev.librarymanagementsystem.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.service.impl.AuthorServiceImpl;

/**
 * Doble de {@link AuthorRepository} que guarda autores en memoria. El catálogo
 * cambia de estado al crear, actualizar y borrar, para encadenar operaciones.
 */
public final class InMemoryAuthorCatalog {

	private final List<Author> authors = new ArrayList<>();
	private final AtomicLong nextId = new AtomicLong(1);
	private final AuthorRepository repository;
	private final AuthorServiceImpl service;

	public InMemoryAuthorCatalog() {
		repository = mock(AuthorRepository.class);
		service = new AuthorServiceImpl(repository);
		when(repository.findAll()).thenAnswer(invocation -> new ArrayList<>(authors));
		when(repository.findById(anyLong())).thenAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			return authors.stream().filter(author -> id.equals(author.getId())).findFirst();
		});
		when(repository.save(any(Author.class))).thenAnswer(invocation -> {
			Author author = invocation.getArgument(0);
			if (author.getId() == null) {
				author.setId(nextId.getAndIncrement());
			}
			authors.removeIf(existing -> author.getId().equals(existing.getId()));
			authors.add(author);
			return author;
		});
		doAnswer(invocation -> {
			Long id = invocation.getArgument(0);
			authors.removeIf(author -> id.equals(author.getId()));
			return null;
		}).when(repository).deleteById(anyLong());
	}

	public AuthorServiceImpl service() {
		return service;
	}

	public AuthorRepository repository() {
		return repository;
	}

	public int size() {
		return authors.size();
	}

	public Optional<Author> findStored(Long id) {
		return authors.stream().filter(author -> id.equals(author.getId())).findFirst();
	}
}
