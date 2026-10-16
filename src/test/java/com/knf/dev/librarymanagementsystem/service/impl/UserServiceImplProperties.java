package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;


class UserServiceImplProperties {

  @Property(tries = 40)
  void elUsernameEsElEmail(@ForAll @StringLength(min = 1, max = 60) String email) {
    UserServiceImpl service = serviceCon(email, new User("Samu", "Test", email, "pwd", List.of()));

    UserDetails details = service.loadUserByUsername(email);

    assertAll(
        "Comprobar que para cualquier email el username devuelto es ese email",
        () -> assertEquals(email, details.getUsername())
    );
  }

  @Property(tries = 40)
  void elPasswordSeConserva(@ForAll @StringLength(max = 60) String password) {
    UserServiceImpl service = serviceCon("samu@test.com",
        new User("Samu", "Test", "samu@test.com", password, List.of()));

    UserDetails details = service.loadUserByUsername("samu@test.com");

    assertAll(
        "Comprobar que para cualquier password el UserDetails lleva ese mismo password",
        () -> assertEquals(password, details.getPassword())
    );
  }

  @Property(tries = 40)
  void cadaRolSeConvierteEnUnaAuthority(
      @ForAll @Size(max = 10) List<@AlphaChars @StringLength(min = 1, max = 20) String> nombresRoles) {
    List<Role> roles = nombresRoles.stream().map(Role::new).collect(Collectors.toList());
    UserServiceImpl service = serviceCon("samu@test.com",
        new User("Samu", "Test", "samu@test.com", "pwd", roles));

    UserDetails details = service.loadUserByUsername("samu@test.com");

    Set<String> authorities = details.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    assertAll(
        "Comprobar que las authorities son exactamente los nombres de los roles (sin repetidos)",
        () -> assertEquals(new HashSet<>(nombresRoles), authorities)
    );
  }

  @Property(tries = 40)
  void usuarioInexistenteLanza(@ForAll @StringLength(max = 60) String email) {
    UserServiceImpl service = serviceCon(email, null);

    assertAll(
        "Comprobar que para cualquier email sin usuario se lanza UsernameNotFoundException",
        () -> assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(email))
    );
  }

  private UserServiceImpl serviceCon(String email, User user) {
    UserRepository repository = mock(UserRepository.class);
    when(repository.findByEmail(email)).thenReturn(user);
    return new UserServiceImpl(repository);
  }
}