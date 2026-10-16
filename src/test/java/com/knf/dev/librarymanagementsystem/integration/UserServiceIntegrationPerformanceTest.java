package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.UserService;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.SimpleTextRenderer;

@SpringBootTest
@Transactional
class UserServiceIntegrationPerformanceTest {

  private static final String PUNTO = "integration.loadUserByUsername.roles=10";

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void sinUsuarios() {
    userRepository.deleteAll();
  }

  @Test
  void testMeasureLoadUserByUsernameAgainstH2() {
    List<Role> roles = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      roles.add(new Role("ROLE_" + i));
    }
    userRepository.save(new User("Samu", "Test", "rend@test.com", "pwd", roles));
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
          userService.loadUserByUsername("rend@test.com");
        } finally {
          point.collect();
        }
      }
      StringWriter buffer = new StringWriter();
      monitor.render(new SimpleTextRenderer(buffer));
      String report = buffer.toString();
      System.out.println(report);

      assertAll(
          "Comprobar que JETM registra loadUserByUsername con un usuario de 10 roles en H2",
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