package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.UserService;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private EntityManager entityManager;

  @BeforeEach
  void sinUsuarios() {
    userRepository.deleteAll();
  }

  private void guardar(String email, String password, String... roles) {
    List<Role> listaRoles = new ArrayList<>();
    for (String role : roles) {
      listaRoles.add(new Role(role));
    }
    userRepository.save(new User("Samu", "Test", email, password, listaRoles));
    entityManager.flush();
    entityManager.clear();
  }

  private Set<String> authorities(UserDetails details) {
    return details.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }

  @Test
  void testLoadUserByUsernameReturnsEmail() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el usuario guardado en H2 se carga con su email como username",
        () -> assertEquals("samu@test.com", details.getUsername())
    );
  }

  @Test
  void testLoadUserByUsernameReturnsStoredPassword() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el password devuelto es el que hay guardado en H2",
        () -> assertEquals("hash123", details.getPassword())
    );
  }

  @Test
  void testEncodedPasswordMatches() {
    guardar("samu@test.com", passwordEncoder.encode("Temp123"), "ROLE_USER");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el password cifrado con BCrypt se valida con el encoder de la aplicación",
        () -> assertTrue(passwordEncoder.matches("Temp123", details.getPassword()))
    );
  }

  @Test
  void testLoadUserWithoutRoles() {
    guardar("samu@test.com", "hash123");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que un usuario guardado sin roles no tiene authorities",
        () -> assertTrue(details.getAuthorities().isEmpty())
    );
  }

  @Test
  void testLoadUserWithOneRole() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el rol guardado en H2 se convierte en una authority",
        () -> assertEquals(Set.of("ROLE_USER"), authorities(details))
    );
  }

  @Test
  void testLoadUserWithTwoRoles() {
    guardar("samu@test.com", "hash123", "ROLE_USER", "ROLE_ADMIN");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que los dos roles guardados en H2 se convierten en dos authorities",
        () -> assertEquals(Set.of("ROLE_USER", "ROLE_ADMIN"), authorities(details))
    );
  }

  @Test
  void testLoadUserAmongSeveral() {
    guardar("ana@test.com", "hashAna", "ROLE_USER");
    guardar("samu@test.com", "hashSamu", "ROLE_ADMIN");

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que con varios usuarios se carga el que corresponde al email",
        () -> assertEquals("hashSamu", details.getPassword())
    );
  }

  @Test
  void testUserDoesNotExist() {
    assertAll(
        "Comprobar que con la tabla vacía se lanza UsernameNotFoundException",
        () -> assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("nadie@test.com"))
    );
  }

  @Test
  void testEmailIsCaseSensitive() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    assertAll(
        "Comprobar que el email distingue mayúsculas y minúsculas",
        () -> assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("SAMU@TEST.COM"))
    );
  }

  @Test
  void testEmailPrefixIsNotFound() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    assertAll(
        "Comprobar que un email al que le falta el último carácter (frontera) no encuentra al usuario",
        () -> assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("samu@test.co"))
    );
  }

  @Test
  void testDuplicateEmailDoesNotFit() {
    guardar("samu@test.com", "hash123", "ROLE_USER");

    assertAll(
        "Comprobar que no se pueden guardar dos usuarios con el mismo email",
        () -> assertThrows(PersistenceException.class, () -> {
          userRepository.save(new User("Otro", "Test", "samu@test.com", "otro", new ArrayList<>()));
          entityManager.flush();
        })
    );
  }
}