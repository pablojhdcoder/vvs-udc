package com.knf.dev.librarymanagementsystem.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.UserService;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.Chars;
import net.jqwik.api.constraints.NumericChars;
import net.jqwik.api.constraints.Size;
import net.jqwik.api.constraints.StringLength;
import net.jqwik.spring.JqwikSpringSupport;

@JqwikSpringSupport
@SpringBootTest
class UserServiceIntegrationProperties {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  private void guardarLimpio(String email, String password, List<Role> roles) {
    User existente = userRepository.findByEmail(email);
    if (existente != null) {
      userRepository.delete(existente);
    }
    userRepository.save(new User("Samu", "Test", email, password, roles));
  }

  @Property(tries = 6)
  void elUsernameEsElEmailGuardado(@ForAll @AlphaChars @StringLength(min = 1, max = 30) String nombre) {
    String email = nombre + "@prop.test";
    guardarLimpio(email, "pwd", new ArrayList<>());

    UserDetails details = userService.loadUserByUsername(email);

    assertAll(
        "Comprobar que para un email aleatorio guardado en H2 el username es ese email",
        () -> assertEquals(email, details.getUsername())
    );
  }

  @Property(tries = 6)
  void elPasswordGuardadoSeConserva(
      @ForAll @AlphaChars @NumericChars @Chars({ '$', '.', '/' }) @StringLength(min = 1, max = 60) String password) {
    guardarLimpio("password@prop.test", password, new ArrayList<>());

    UserDetails details = userService.loadUserByUsername("password@prop.test");

    assertAll(
        "Comprobar que un password aleatorio guardado en H2 se devuelve tal cual",
        () -> assertEquals(password, details.getPassword())
    );
  }

  @Property(tries = 6)
  void cadaRolGuardadoEsUnaAuthority(
      @ForAll @Size(max = 5) List<@AlphaChars @StringLength(min = 1, max = 15) String> nombresRoles) {
    List<Role> roles = nombresRoles.stream().map(Role::new).collect(Collectors.toList());
    guardarLimpio("roles@prop.test", "pwd", roles);

    UserDetails details = userService.loadUserByUsername("roles@prop.test");

    Set<String> authorities = details.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
    assertAll(
        "Comprobar que los roles aleatorios guardados en H2 son exactamente las authorities",
        () -> assertEquals(new HashSet<>(nombresRoles), authorities)
    );
  }

  @Property(tries = 5)
  void emailNoGuardadoLanza(@ForAll @AlphaChars @StringLength(min = 1, max = 30) String nombre) {
    String email = nombre + "@no-existe.test";

    assertAll(
        "Comprobar que un email aleatorio que no está en H2 lanza UsernameNotFoundException",
        () -> assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email))
    );
  }
}