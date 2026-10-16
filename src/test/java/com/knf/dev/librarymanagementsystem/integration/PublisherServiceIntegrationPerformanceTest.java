package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.PublisherService;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

@SpringBootTest
@Transactional
class PublisherServiceIntegrationPerformanceTest {

  private static final String PUNTO = "integration.findAllPublishers.n=25";

  @Autowired
  private PublisherService publisherService;

  @Autowired
  private PublisherRepository publisherRepository;

  @Autowired
  private BookRepository bookRepository;

  @BeforeEach
  void catalogoVacio() {
    bookRepository.deleteAll();
    publisherRepository.deleteAll();
  }

  @Test
  void testMeasureFindAllPublishersAgainstH2() {
    for (int i = 0; i < 25; i++) {
      publisherService.createPublisher(new Publisher("Rend-" + i));
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
          publisherService.findAllPublishers();
        } finally {
          point.collect();
        }
      }
      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));
      String report = buffer.toString();
      System.out.println(report);

      assertAll(
          "Comprobar que JETM registra findAllPublishers con 25 editoriales en H2",
          () -> assertTrue(report.contains(PUNTO))
      );
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
    }
    return EtmManager.getEtmMonitor();
  }
}