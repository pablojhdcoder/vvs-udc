package com.knf.dev.librarymanagementsystem.quality;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.impl.UserServiceImpl;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

class UserServiceImplPerformanceTest {

  @Test
  void testMeasureLoadUserByUsernameWithGrowingRoles() {
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
      measure(monitor, "user.loadUserByUsername.roles=10", 10, 50);
      measure(monitor, "user.loadUserByUsername.roles=1000", 1000, 20);

      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));
      String report = buffer.toString();
      System.out.println(report);

      assertAll(
          "Comprobar que JETM registra las medidas con 10 y 1000 roles",
          () -> assertTrue(report.contains("user.loadUserByUsername.roles=10")),
          () -> assertTrue(report.contains("user.loadUserByUsername.roles=1000"))
      );
    } finally {
      if (arrancadoAqui) {
        monitor.stop();
      }
    }
  }

  private void measure(EtmMonitor monitor, String pointName, int roles, int repetitions) {
    UserRepository repository = mock(UserRepository.class);
    UserServiceImpl service = new UserServiceImpl(repository);
    when(repository.findByEmail("samu@test.com"))
        .thenReturn(new User("Samu", "Test", "samu@test.com", "pwd", listOfRoles(roles)));

    for (int i = 0; i < repetitions; i++) {
      EtmPoint point = monitor.createPoint(pointName);
      try {
        service.loadUserByUsername("samu@test.com");
      } finally {
        point.collect();
      }
    }
  }

  private List<Role> listOfRoles(int n) {
    List<Role> roles = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      roles.add(new Role("ROLE_" + i));
    }
    return roles;
  }
}