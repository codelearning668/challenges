package sk.mkrajcovic.challenges.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ClientExceptionHierarchyTest {

	private static final String ERROR = "ERROR";
	private static final String ARG = "foo";

	@ParameterizedTest(name = "{0} should support all constructors")
	@MethodSource("exceptionTypes")
	void shouldSupportAllConstructors(String name, ExceptionFactory factory) {
		Throwable cause = new IOException("cause");
		ClientException codeOnly = factory.create(ERROR);
		ClientException withArgs = factory.create(ERROR, ARG, 123);
		ClientException withCause = factory.createWithCause(ERROR, cause, ARG, 123);

		assertAll(() -> {
			assertEquals(ERROR, codeOnly.getCode());
			assertArrayEquals(new Object[0], codeOnly.getArgs());
			assertEquals("ERROR: []", codeOnly.getMessage());
			assertNull(codeOnly.getCause());
		}, () -> {
			assertEquals(ERROR, withArgs.getCode());
			assertArrayEquals(new Object[] { ARG, 123 }, withArgs.getArgs());
			assertEquals("ERROR: [foo, 123]", withArgs.getMessage());
			assertNull(withArgs.getCause());
		}, () -> {
			assertEquals(ERROR, withCause.getCode());
			assertArrayEquals(new Object[] { ARG, 123 }, withCause.getArgs());
			assertEquals("ERROR: [foo, 123]", withCause.getMessage());
			assertSame(cause, withCause.getCause());
		});
	}

	@ParameterizedTest(name = "{0} should support equals and hashCode")
	@MethodSource("exceptionTypes")
	void shouldSupportEqualsAndHashCode(String name, ExceptionFactory factory) {
		ClientException first = factory.create(ERROR, ARG, 123);
		ClientException second = factory.create(ERROR, ARG, 123);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void differentExceptionTypesShouldNotBeEqual() {
		assertNotEquals(new BusinessViolation(ERROR, ARG), new AccessDenied(ERROR, ARG));
		assertNotEquals(new BusinessViolation(ERROR, ARG), new Conflict(ERROR, ARG));
		assertNotEquals(new BusinessViolation(ERROR, ARG), new ResourceNotFound(ERROR, ARG));
		assertNotEquals(new AccessDenied(ERROR, ARG), new Conflict(ERROR, ARG));
		assertNotEquals(new AccessDenied(ERROR, ARG), new ResourceNotFound(ERROR, ARG));
		assertNotEquals(new Conflict(ERROR, ARG), new ResourceNotFound(ERROR, ARG));
	}

	@Test
	void clientExceptionAndSubclassShouldNotBeEqual() {
		ClientException base = new ClientException(ERROR, ARG);
		ClientException subclass = new BusinessViolation(ERROR, ARG);

		assertNotEquals(base, subclass);
		assertNotEquals(subclass, base);
	}

	@Test
	void allExceptionTypesShouldExtendClientException() {
		assertAll(() -> assertInstanceOf(ClientException.class, new BusinessViolation(ERROR)),
				() -> assertInstanceOf(ClientException.class, new AccessDenied(ERROR)),
				() -> assertInstanceOf(ClientException.class, new Conflict(ERROR)),
				() -> assertInstanceOf(ClientException.class, new ResourceNotFound(ERROR)));
	}

	@Test
	void allExceptionTypesShouldBeRuntimeExceptions() {
		assertAll(() -> assertInstanceOf(RuntimeException.class, new BusinessViolation(ERROR)),
				() -> assertInstanceOf(RuntimeException.class, new AccessDenied(ERROR)),
				() -> assertInstanceOf(RuntimeException.class, new Conflict(ERROR)),
				() -> assertInstanceOf(RuntimeException.class, new ResourceNotFound(ERROR)));
	}

	private static Stream<Arguments> exceptionTypes() {
		return Stream.of(
			Arguments.of("BusinessViolation", createBusinessViolationExceptionFactory()),
			Arguments.of("AccessDenied", createAccessDeniedExceptionFactory()),
			Arguments.of("Conflict", createConflictExceptionFactory()),
			Arguments.of("ResourceNotFound", createResourceNotFoundExceptionFactory()));
	}

	private interface ExceptionFactory {
		ClientException create(String code, Object... args);
		ClientException createWithCause(String code, Throwable cause, Object... args);
	}
	
	private static ExceptionFactory createBusinessViolationExceptionFactory() {
		return new ExceptionFactory() {
			@Override
			public ClientException create(String code, Object... args) {
				return new BusinessViolation(code, args);
			}

			@Override
			public ClientException createWithCause(String code, Throwable cause, Object... args) {
				return new BusinessViolation(code, cause, args);
			}
		};
	}

	private static ExceptionFactory createAccessDeniedExceptionFactory() {
		return new ExceptionFactory() {
			@Override
			public ClientException create(String code, Object... args) {
				return new AccessDenied(code, args);
			}

			@Override
			public ClientException createWithCause(String code, Throwable cause, Object... args) {
				return new AccessDenied(code, cause, args);
			}
		};
	}

	private static ExceptionFactory createConflictExceptionFactory() {
		return new ExceptionFactory() {
			@Override
			public ClientException create(String code, Object... args) {
				return new Conflict(code, args);
			}

			@Override
			public ClientException createWithCause(String code, Throwable cause, Object... args) {
				return new Conflict(code, cause, args);
			}
		};
	}

	private static ExceptionFactory createResourceNotFoundExceptionFactory() {
		return new ExceptionFactory() {
			@Override
			public ClientException create(String code, Object... args) {
				return new ResourceNotFound(code, args);
			}

			@Override
			public ClientException createWithCause(String code, Throwable cause, Object... args) {
				return new ResourceNotFound(code, cause, args);
			}
		};
	}
}
