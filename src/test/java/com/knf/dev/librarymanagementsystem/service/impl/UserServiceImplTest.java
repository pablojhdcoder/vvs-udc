package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  UserRepository userRepository;

  @InjectMocks
  UserServiceImpl userService;

  private User user(String email, String password, List<Role> roles) {
    return new User("Samu", "Test", email, password, roles);
  }

  private Set<String> authorities(UserDetails details) {
    return details.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }


  @Test
  void testLoadUserByUsernameReturnsEmailAsUsername() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123", List.of(new Role("ROLE_USER"))));

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el username del UserDetails es el email del usuario",
        () -> assertEquals("samu@test.com", details.getUsername())
    );
  }

  @Test
  void testLoadUserByUsernameReturnsPassword() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123", List.of(new Role("ROLE_USER"))));

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que el password del UserDetails es el del usuario",
        () -> assertEquals("hash123", details.getPassword())
    );
  }

  @Test
  void testLoadUserByUsernameSearchesByEmail() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123", List.of(new Role("ROLE_USER"))));

    userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que se busca al usuario en el repositorio por su email",
        () -> verify(userRepository).findByEmail("samu@test.com")
    );
  }

  @Test
  void testLoadUserByUsernameWithNoRoles() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123", List.of()));

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que un usuario sin roles no tiene authorities",
        () -> assertTrue(details.getAuthorities().isEmpty())
    );
  }

  @Test
  void testLoadUserByUsernameWithOneRole() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123", List.of(new Role("ROLE_USER"))));

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que un usuario con un rol tiene exactamente esa authority",
        () -> assertEquals(Set.of("ROLE_USER"), authorities(details))
    );
  }

  @Test
  void testLoadUserByUsernameWithTwoRoles() {
    when(userRepository.findByEmail("samu@test.com"))
        .thenReturn(user("samu@test.com", "hash123",
            List.of(new Role("ROLE_USER"), new Role("ROLE_ADMIN"))));

    UserDetails details = userService.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que un usuario con dos roles tiene esas dos authorities",
        () -> assertEquals(Set.of("ROLE_USER", "ROLE_ADMIN"), authorities(details))
    );
  }


  @Test
  void testLoadUserByUsernameWhenUserDoesNotExist() {
    when(userRepository.findByEmail("nadie@test.com")).thenReturn(null);

    assertAll(
        "Comprobar que se lanza UsernameNotFoundException si el usuario no existe",
        () -> assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("nadie@test.com"))
    );
  }

  @Test
  void testLoadUserByUsernameNotFoundMessage() {
    when(userRepository.findByEmail("nadie@test.com")).thenReturn(null);

    assertAll(
        "Comprobar que el mensaje no revela si falla el usuario o la contraseña",
        () -> assertEquals("Invalid username or password.",
            assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nadie@test.com")).getMessage())
    );
  }

  @Test
  void testLoadUserByUsernameWithNullUsername() {
    when(userRepository.findByEmail(null)).thenReturn(null);

    assertAll(
        "Comprobar que con username null también se lanza UsernameNotFoundException",
        () -> assertThrows(UsernameNotFoundException.class,
            () -> userService.loadUserByUsername(null))
    );
  }
}