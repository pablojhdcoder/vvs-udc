package com.knf.dev.librarymanagementsystem.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

	@Nested
	@DisplayName("constructor con mensaje")
	class ConstructorConMensaje {

		@Test
		@DisplayName("conserva el mensaje")
		void conservaElMensaje() {
			assertEquals("Category not found with ID 1",
					new NotFoundException("Category not found with ID 1").getMessage());
		}

		@Test
		@DisplayName("mensaje vacío se conserva")
		void mensajeVacio() {
			assertEquals("", new NotFoundException("").getMessage());
		}

		@Test
		@DisplayName("mensaje null se conserva")
		void mensajeNull() {
			assertNull(new NotFoundException(null).getMessage());
		}

		@Test
		@DisplayName("es una RuntimeException")
		void esRuntimeException() {
			assertTrue(new NotFoundException("x") instanceof RuntimeException);
		}
	}
}
