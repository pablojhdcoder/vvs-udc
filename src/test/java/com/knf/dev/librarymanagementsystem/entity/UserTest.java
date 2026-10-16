package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

  private final Role roleUser = new Role("ROLE_USER");
  private final Role roleAdmin = new Role("ROLE_ADMIN");
  private final Role roleGuest = new Role("ROLE_GUEST");

  static Stream<String> textosFrontera() {
    return Stream.of("", "a", "a".repeat(254), "a".repeat(255), "a".repeat(256));
  }

  private User usuario() {
    return new User("Samu", "Test", "samu@test.com", "hash123", new ArrayList<>());
  }

  @Test
  void shouldCreateEmptyUser() {
    User user = new User();

    assertAll(
        "Comprobar que el constructor vacío deja todos los campos a null, incluida la colección de roles",
        () -> assertNull(user.getId()),
        () -> assertNull(user.getFirstName()),
        () -> assertNull(user.getLastName()),
        () -> assertNull(user.getEmail()),
        () -> assertNull(user.getPassword()),
        () -> assertNull(user.getRoles())
    );
  }

  @Test
  void shouldCreateUserWithAllFields() {
    List<Role> roles = new ArrayList<>(List.of(roleUser));
    User user = new User("Samu", "Test", "samu@test.com", "hash123", roles);

    assertAll(
        "Comprobar que el constructor completo guarda nombre, apellido, email, password y roles",
        () -> assertEquals("Samu", user.getFirstName()),
        () -> assertEquals("Test", user.getLastName()),
        () -> assertEquals("samu@test.com", user.getEmail()),
        () -> assertEquals("hash123", user.getPassword()),
        () -> assertSame(roles, user.getRoles())
    );
  }

  @Test
  void shouldCreateUserWithoutId() {
    User user = usuario();

    assertAll(
        "Comprobar que el constructor completo deja el ID a null",
        () -> assertNull(user.getId())
    );
  }

  @Test
  void shouldCreateUserWithNullFields() {
    User user = new User(null, null, null, null, null);

    assertAll(
        "Comprobar que el constructor completo acepta todos los campos a null",
        () -> assertNull(user.getFirstName()),
        () -> assertNull(user.getLastName()),
        () -> assertNull(user.getEmail()),
        () -> assertNull(user.getPassword()),
        () -> assertNull(user.getRoles())
    );
  }

  @ParameterizedTest
  @ValueSource(longs = { Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE })
  void shouldSetAndGetId(long id) {
    User user = usuario();

    user.setId(id);

    assertAll(
        "Comprobar que se puede establecer y obtener el ID en sus fronteras",
        () -> assertEquals(id, user.getId())
    );
  }

  @Test
  void shouldSetIdToNull() {
    User user = usuario();
    user.setId(5L);

    user.setId(null);

    assertAll(
        "Comprobar que se puede volver a poner el ID a null",
        () -> assertNull(user.getId())
    );
  }

  @ParameterizedTest
  @MethodSource("textosFrontera")
  void shouldSetAndGetBoundaryFirstName(String firstName) {
    User user = usuario();

    user.setFirstName(firstName);

    assertAll(
        "Comprobar que setFirstName guarda textos en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(firstName, user.getFirstName())
    );
  }

  @Test
  void shouldSetFirstNameToNull() {
    User user = usuario();

    user.setFirstName(null);

    assertAll(
        "Comprobar que se puede poner el nombre a null",
        () -> assertNull(user.getFirstName())
    );
  }

  @ParameterizedTest
  @MethodSource("textosFrontera")
  void shouldSetAndGetBoundaryLastName(String lastName) {
    User user = usuario();

    user.setLastName(lastName);

    assertAll(
        "Comprobar que setLastName guarda textos en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(lastName, user.getLastName())
    );
  }

  @Test
  void shouldSetLastNameToNull() {
    User user = usuario();

    user.setLastName(null);

    assertAll(
        "Comprobar que se puede poner el apellido a null",
        () -> assertNull(user.getLastName())
    );
  }

  @ParameterizedTest
  @MethodSource("textosFrontera")
  void shouldSetAndGetBoundaryEmail(String email) {
    User user = usuario();

    user.setEmail(email);

    assertAll(
        "Comprobar que setEmail guarda textos en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(email, user.getEmail())
    );
  }

  @Test
  void shouldStoreEmailWithoutValidatingFormat() {
    User user = usuario();

    user.setEmail("esto-no-es-un-email");

    assertAll(
        "Comprobar que la entidad no valida el formato del email y lo guarda tal cual",
        () -> assertEquals("esto-no-es-un-email", user.getEmail())
    );
  }

  @Test
  void shouldSetEmailToNull() {
    User user = usuario();

    user.setEmail(null);

    assertAll(
        "Comprobar que se puede poner el email a null",
        () -> assertNull(user.getEmail())
    );
  }

  @ParameterizedTest
  @MethodSource("textosFrontera")
  void shouldSetAndGetBoundaryPassword(String password) {
    User user = usuario();

    user.setPassword(password);

    assertAll(
        "Comprobar que setPassword guarda textos en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(password, user.getPassword())
    );
  }

  @Test
  void shouldSetPasswordToNull() {
    User user = usuario();

    user.setPassword(null);

    assertAll(
        "Comprobar que se puede poner el password a null",
        () -> assertNull(user.getPassword())
    );
  }

  @Test
  void shouldSetAndGetEmptyRoles() {
    User user = usuario();
    List<Role> roles = new ArrayList<>();

    user.setRoles(roles);

    assertAll(
        "Comprobar que se puede establecer y obtener una colección de roles vacía",
        () -> assertEquals(roles, user.getRoles()),
        () -> assertTrue(user.getRoles().isEmpty())
    );
  }

  @Test
  void shouldSetAndGetOneRole() {
    User user = usuario();
    List<Role> roles = new ArrayList<>(List.of(roleUser));

    user.setRoles(roles);

    assertAll(
        "Comprobar que un usuario puede tener exactamente un rol",
        () -> assertEquals(roles, user.getRoles()),
        () -> assertEquals(1, user.getRoles().size())
    );
  }

  @Test
  void shouldSetAndGetTwoRoles() {
    User user = usuario();
    List<Role> roles = new ArrayList<>(List.of(roleUser, roleAdmin));

    user.setRoles(roles);

    assertAll(
        "Comprobar que un usuario puede tener exactamente dos roles",
        () -> assertEquals(roles, user.getRoles()),
        () -> assertEquals(2, user.getRoles().size())
    );
  }

  @Test
  void shouldSetAndGetThreeRoles() {
    User user = usuario();
    List<Role> roles = new ArrayList<>(List.of(roleUser, roleAdmin, roleGuest));

    user.setRoles(roles);

    assertAll(
        "Comprobar que un usuario puede tener exactamente tres roles",
        () -> assertEquals(roles, user.getRoles()),
        () -> assertEquals(3, user.getRoles().size())
    );
  }

  @Test
  void shouldKeepSameRoleTwiceInList() {
    User user = usuario();
    List<Role> roles = new ArrayList<>();
    roles.add(roleUser);
    roles.add(roleUser);

    user.setRoles(roles);

    assertAll(
        "Comprobar que al ser una lista, la misma instancia de rol puede aparecer dos veces",
        () -> assertEquals(2, user.getRoles().size())
    );
  }

  @Test
  void shouldKeepSameRolesInstance() {
    User user = usuario();
    List<Role> roles = new ArrayList<>(List.of(roleUser));

    user.setRoles(roles);

    assertAll(
        "Comprobar que setRoles guarda la misma instancia de la colección, sin copiarla",
        () -> assertSame(roles, user.getRoles())
    );
  }

  @Test
  void shouldReplacePreviousRoles() {
    User user = new User("Samu", "Test", "samu@test.com", "hash123", new ArrayList<>(List.of(roleUser)));
    List<Role> nuevosRoles = new ArrayList<>(List.of(roleAdmin, roleGuest));

    user.setRoles(nuevosRoles);

    assertAll(
        "Comprobar que setRoles sustituye la colección anterior",
        () -> assertEquals(nuevosRoles, user.getRoles())
    );
  }

  @Test
  void shouldSetRolesToNull() {
    User user = usuario();

    user.setRoles(null);

    assertAll(
        "Comprobar que se puede poner la colección de roles a null",
        () -> assertNull(user.getRoles())
    );
  }
}