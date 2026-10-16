package com.knf.dev.librarymanagementsystem.quality;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.repository.PublisherRepository;
import com.knf.dev.librarymanagementsystem.service.impl.PublisherServiceImpl;
import com.knf.dev.librarymanagementsystem.util.Mapper;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

class PublisherServiceImplPerformanceTest {

  @Test
  void testMeasureFindAllPublishersAndMapperWithGrowingLists() {
    try {
      BasicEtmConfigurator.configure();
    } catch (IllegalStateException alreadyConfigured) {
    }
    EtmMonitor monitor = EtmManager.getEtmMonitor();
    boolean arrancadoAqui = false;
    if (!monitor.isStarted()) {
      monitor.start();
      arrancadoAqui = true;
    }
    try {
      measure(monitor, "publisher.findAllYMapper.n=100", 100, 50);
      measure(monitor, "publisher.findAllYMapper.n=5000", 5000, 20);

      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));
      String report = buffer.toString();
      System.out.println(report);

      assertAll(
          "Comprobar que JETM registra las medidas con 100 y 5000 editoriales",
          () -> assertTrue(report.contains("publisher.findAllYMapper.n=100")),
          () -> assertTrue(report.contains("publisher.findAllYMapper.n=5000"))
      );
    } finally {
      if (arrancadoAqui) {
        monitor.stop();
      }
    }
  }

  private void measure(EtmMonitor monitor, String pointName, int publishers, int repetitions) {
    PublisherRepository repository = mock(PublisherRepository.class);
    PublisherServiceImpl service = new PublisherServiceImpl(repository);
    when(repository.findAll()).thenReturn(listOfPublishers(publishers));

    for (int i = 0; i < repetitions; i++) {
      EtmPoint point = monitor.createPoint(pointName);
      try {
        Mapper.publisherModelToVo(service.findAllPublishers());
      } finally {
        point.collect();
      }
    }
  }

  private List<Publisher> listOfPublishers(int n) {
    List<Publisher> publishers = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      Publisher publisher = new Publisher("publisher-" + i);
      publisher.setId((long) i);
      publishers.add(publisher);
    }
    return publishers;
  }
}