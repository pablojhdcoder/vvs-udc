package com.knf.dev.librarymanagementsystem.quality;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.service.impl.AuthorServiceImpl;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

class AuthorServiceImplPerformanceTest {

	@Test
	void measureFindPaginatedWithGrowingLists() {
		BasicEtmConfigurator.configure();
		EtmMonitor monitor = EtmManager.getEtmMonitor();
		monitor.start();
		try {
			measure(monitor, "findPaginated.n=100", 100, 50);
			measure(monitor, "findPaginated.n=5000", 5000, 20);

			StringWriter buffer = new StringWriter();
			monitor.render(new SimpleTextRenderer(buffer));
			String report = buffer.toString();

			assertTrue(report.contains("findPaginated.n=100"));
			assertTrue(report.contains("findPaginated.n=5000"));
			assertTrue(report.contains("50"));
			assertTrue(report.contains("20"));
			System.out.println(report);
		} finally {
			monitor.stop();
		}
	}

	private void measure(EtmMonitor monitor, String pointName, int authors, int repetitions) {
		AuthorRepository repository = mock(AuthorRepository.class);
		AuthorServiceImpl service = new AuthorServiceImpl(repository);
		when(repository.findAll()).thenReturn(listOfAuthors(authors));

		for (int i = 0; i < repetitions; i++) {
			EtmPoint point = monitor.createPoint(pointName);
			try {
				service.findPaginated(PageRequest.of(0, 20));
			} finally {
				point.collect();
			}
		}
	}

	private List<Author> listOfAuthors(int n) {
		List<Author> authors = new ArrayList<>(n);
		for (int i = 0; i < n; i++) {
			Author author = new Author("author-" + i, "desc");
			author.setId((long) i);
			authors.add(author);
		}
		return authors;
	}
}
