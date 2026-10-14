package com.knf.dev.librarymanagementsystem.quality;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.impl.BookServiceImpl;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

/**
 * Rendimiento de {@link BookServiceImpl#findPaginated} con JETM. La paginación lee todo
 * findAll en memoria; se mide con listas de 100 y 5000 libros.
 */
class BookServiceImplPerformanceTest {

  @Test
  @DisplayName("JETM: findPaginated con listas crecientes aparece en el informe")
  void medirFindPaginatedConListasCrecientes() {
    BasicEtmConfigurator.configure();
    EtmMonitor monitor = EtmManager.getEtmMonitor();
    monitor.start();
    try {
      medir(monitor, "findPaginated.n=100", 100, 50);
      medir(monitor, "findPaginated.n=5000", 5000, 20);

      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));
      String report = buffer.toString();

      assertAll(
          () -> assertTrue(report.contains("findPaginated.n=100")),
          () -> assertTrue(report.contains("findPaginated.n=5000")),
          () -> assertTrue(report.contains("50")),
          () -> assertTrue(report.contains("20")));
      System.out.println(report);
    } finally {
      monitor.stop();
    }
  }

  private void medir(EtmMonitor monitor, String pointName, int books, int repetitions) {
    BookRepository repository = mock(BookRepository.class);
    BookServiceImpl service = new BookServiceImpl(repository);
    when(repository.findAll()).thenReturn(listaDeLibros(books));

    for (int i = 0; i < repetitions; i++) {
      EtmPoint point = monitor.createPoint(pointName);
      try {
        service.findPaginated(PageRequest.of(0, 20));
      } finally {
        point.collect();
      }
    }
  }

  private List<Book> listaDeLibros(int n) {
    List<Book> books = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      Book book = new Book("ISBN-" + i, "Libro " + i, "S" + i, "descripcion");
      book.setId((long) i);
      books.add(book);
    }
    return books;
  }
}
