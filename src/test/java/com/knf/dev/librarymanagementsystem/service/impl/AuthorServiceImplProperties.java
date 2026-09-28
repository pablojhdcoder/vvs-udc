package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.exception.NotFoundException;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;
import net.jqwik.api.constraints.StringLength;

class AuthorServiceImplProperties {

	@Property(tries = 40)
	void findPaginatedNeverExceedsPageSizeAndKeepsTotal(
			@ForAll @IntRange(min = 0, max = 25) int n,
			@ForAll @IntRange(min = 0, max = 8) int page,
			@ForAll @IntRange(min = 1, max = 10) int size) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		List<Author> all = authors(n);
		when(repository.findAll()).thenReturn(all);

		Page<Author> result = service.findPaginated(PageRequest.of(page, size));

		assertTrue(result.getContent().size() <= size);
		assertEquals(n, result.getTotalElements());
		assertEquals(page, result.getNumber());
		assertEquals(size, result.getSize());

		int start = page * size;
		if (start >= n) {
			assertTrue(result.getContent().isEmpty());
		} else {
			int toIndex = Math.min(start + size, n);
			assertEquals(all.subList(start, toIndex), result.getContent());
		}
	}

	@Property(tries = 30)
	void findAuthorByIdThrowsWhenRepositoryIsEmpty(@ForAll @LongRange(min = 1, max = 10_000) long id) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		when(repository.findById(id)).thenReturn(Optional.empty());

		NotFoundException ex = assertThrows(NotFoundException.class, () -> service.findAuthorById(id));
		assertTrue(ex.getMessage().contains(String.valueOf(id)));
	}

	@Property(tries = 30)
	void createAuthorAlwaysDelegatesToSave(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		Author author = new Author(name, description);

		service.createAuthor(author);

		verify(repository).save(author);
	}

	@Property(tries = 30)
	void updateAuthorAlwaysDelegatesToSave(
			@ForAll @StringLength(min = 1, max = 40) String name,
			@ForAll @StringLength(min = 1, max = 80) String description) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		Author author = new Author(name, description);

		service.updateAuthor(author);

		verify(repository).save(author);
	}

	private List<Author> authors(int n) {
		List<Author> authors = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			Author author = new Author("author-" + i, "desc");
			author.setId((long) i);
			authors.add(author);
		}
		return authors;
	}
}
