package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.repository.AuthorRepository;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.service.AuthorService;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rendimiento de findPaginated contra H2. La prueba de unidad mide el mismo método con un mock;
 * aquí el tiempo incluye la lectura real de la tabla.
 */
@SpringBootTest
@Transactional
class AuthorServiceIntegrationPerformanceTest {

  private static final String PUNTO = "integration.findPaginated.n=25";

  @Autowired private AuthorService authorService;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private BookRepository bookRepository;

  @BeforeEach
  void catalogoVacio() {
    bookRepository.deleteAll();
    authorRepository.deleteAll();
  }

  @Test
  @DisplayName("JETM registra findPaginated con 25 autores persistidos")
  void mideFindPaginatedContraH2() {
    for (int i = 0; i < 25; i++) {
      authorService.createAuthor(new Author("Rend-" + i, "d"));
    }
    EtmMonitor monitor = monitor();
    boolean arrancadoAqui = false;
    if (!monitor.isStarted()) {
      monitor.start();
      arrancadoAqui = true;
    }
    try {
      for (int i = 0; i < 10; i++) {
        EtmPoint point = monitor.createPoint(PUNTO);
        try {
          authorService.findPaginated(PageRequest.of(0, 10));
        } finally {
          point.collect();
        }
      }
      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));

      assertTrue(buffer.toString().contains(PUNTO));
    } finally {
      if (arrancadoAqui) {
        monitor.stop();
      }
    }
  }

  private static EtmMonitor monitor() {
    try {
      BasicEtmConfigurator.configure();
    } catch (IllegalStateException alreadyConfigured) {
      // la prueba de unidad puede haber arrancado JETM en la misma JVM
    }
    return etm.core.configuration.EtmManager.getEtmMonitor();
  }
}
