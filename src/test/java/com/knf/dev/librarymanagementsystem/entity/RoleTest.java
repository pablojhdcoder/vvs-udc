package com.knf.dev.librarymanagementsystem.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class RoleTest {

  static Stream<String> nombresFrontera() {
    return Stream.of("", "a", "a".repeat(254), "a".repeat(255), "a".repeat(256));
  }

  @Test
  void shouldCreateEmptyRole() {
    Role role = new Role();

    assertAll(
        "Comprobar que el constructor vacío deja id y name a null",
        () -> assertNull(role.getId()),
        () -> assertNull(role.getName())
    );
  }

  @Test
  void shouldCreateRoleWithName() {
    Role role = new Role("ROLE_ADMIN");

    assertAll(
        "Comprobar que el constructor con nombre guarda el name y deja el id a null",
        () -> assertEquals("ROLE_ADMIN", role.getName()),
        () -> assertNull(role.getId())
    );
  }

  @ParameterizedTest
  @MethodSource("nombresFrontera")
  void shouldCreateRoleWithBoundaryName(String name) {
    Role role = new Role(name);

    assertAll(
        "Comprobar que el constructor guarda nombres en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(name, role.getName())
    );
  }

  @Test
  void shouldCreateRoleWithNullName() {
    Role role = new Role(null);

    assertAll(
        "Comprobar que el constructor acepta un nombre null",
        () -> assertNull(role.getName())
    );
  }

  @Test
  void shouldStoreNameWithoutPrefix() {
    Role role = new Role("ADMIN");

    assertAll(
        "Comprobar que la entidad no exige el prefijo ROLE_ y guarda el nombre tal cual",
        () -> assertEquals("ADMIN", role.getName())
    );
  }

  @ParameterizedTest
  @ValueSource(longs = { Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE })
  void shouldSetAndGetId(long id) {
    Role role = new Role("ROLE_USER");

    role.setId(id);

    assertAll(
        "Comprobar que se puede establecer y obtener el ID en sus fronteras",
        () -> assertEquals(id, role.getId())
    );
  }

  @Test
  void shouldSetIdToNull() {
    Role role = new Role("ROLE_USER");
    role.setId(5L);

    role.setId(null);

    assertAll(
        "Comprobar que se puede volver a poner el ID a null",
        () -> assertNull(role.getId())
    );
  }

  @ParameterizedTest
  @MethodSource("nombresFrontera")
  void shouldSetAndGetBoundaryName(String name) {
    Role role = new Role();

    role.setName(name);

    assertAll(
        "Comprobar que setName guarda nombres en la frontera de longitud (0, 1, 254, 255, 256)",
        () -> assertEquals(name, role.getName())
    );
  }

  @Test
  void shouldChangeName() {
    Role role = new Role("ROLE_USER");

    role.setName("ROLE_ADMIN");

    assertAll(
        "Comprobar que setName sustituye el nombre anterior",
        () -> assertEquals("ROLE_ADMIN", role.getName())
    );
  }

  @Test
  void shouldSetNameToNull() {
    Role role = new Role("ROLE_USER");

    role.setName(null);

    assertAll(
        "Comprobar que se puede poner el nombre a null",
        () -> assertNull(role.getName())
    );
  }

  @Test
  void shouldNotBeEqualWithSameName() {
    Role role1 = new Role("ROLE_USER");
    Role role2 = new Role("ROLE_USER");

    assertAll(
        "Comprobar que dos roles con el mismo nombre son objetos distintos (Role no redefine equals)",
        () -> assertNotEquals(role1, role2)
    );
  }
}