package com.knf.dev.librarymanagementsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;

class UserServiceImplTest {

    private UserRepository userRepository;
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		userService = new UserServiceImpl(userRepository);
	}

    private User usuario(String email, String password, List<Role> roles) {
		User user = mock(User.class);
		when(user.getEmail()).thenReturn(email);
		when(user.getPassword()).thenReturn(password);
		when(user.getRoles()).thenReturn(roles);
		return user;
	}

    private Set<String> nombresDeAuthorities(UserDetails details) {
		return details.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());
	}@Nested
	@DisplayName("loadUserByUsername: el usuario existe")
	class Existe {

		@Test
		@DisplayName("devuelve UserDetails con email, password y los roles como authorities")
		void usuarioConRoles() {
			User user = usuario("samu@test.com", "hash123",
					List.of(new Role("ROLE_USER"), new Role("ROLE_ADMIN")));
			when(userRepository.findByEmail("samu@test.com")).thenReturn(user);

			UserDetails details = userService.loadUserByUsername("samu@test.com");

			assertEquals("samu@test.com", details.getUsername());
			assertEquals("hash123", details.getPassword());
			assertEquals(Set.of("ROLE_USER", "ROLE_ADMIN"), nombresDeAuthorities(details));
			verify(userRepository).findByEmail("samu@test.com");
		}

		@Test
		@DisplayName("un usuario sin roles devuelve authorities vacías")
		void usuarioSinRoles() {
			User user = usuario("samu@test.com", "hash123", List.of());
			when(userRepository.findByEmail("samu@test.com")).thenReturn(user);

			UserDetails details = userService.loadUserByUsername("samu@test.com");

			assertTrue(details.getAuthorities().isEmpty());
		}
	}

	@Nested
	@DisplayName("loadUserByUsername: el usuario no existe")
	class NoExiste {

		@Test
		@DisplayName("lanza UsernameNotFoundException si findByEmail devuelve null")
		void usuarioInexistente() {
			when(userRepository.findByEmail("nadie@test.com")).thenReturn(null);

			UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
					() -> userService.loadUserByUsername("nadie@test.com"));
			assertEquals("Invalid username or password.", ex.getMessage());
		}
	}
}
