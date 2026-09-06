package sk.mkrajcovic.challenges.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.IOException;

import org.junit.jupiter.api.Test;

class ClientExceptionTest {

	private static final String ERROR = "ERROR";
	private static final String CLIENT_ERROR = "CLIENT_ERROR";
	private static final String INVALID_VALUE = "INVALID_VALUE";

	@Test
	void shouldCreateWithCodeOnly() {
		ClientException exception = new ClientException(CLIENT_ERROR);

		assertAll(() -> assertEquals(CLIENT_ERROR, exception.getCode()),
				() -> assertArrayEquals(new Object[0], exception.getArgs()),
				() -> assertEquals("CLIENT_ERROR: []", exception.getMessage()), () -> assertNull(exception.getCause()));
	}

	@Test
	void shouldCreateWithCodeAndArgs() {
		ClientException exception = new ClientException(INVALID_VALUE, "username", 42, true);

		assertAll(() -> assertEquals(INVALID_VALUE, exception.getCode()),
				() -> assertArrayEquals(new Object[] { "username", 42, true }, exception.getArgs()),
				() -> assertEquals("INVALID_VALUE: [username, 42, true]", exception.getMessage()),
				() -> assertNull(exception.getCause()));
	}

	@Test
	void shouldCreateWithCauseAndArgs() {
		Throwable cause = new IOException("Something went wrong");
		ClientException exception = new ClientException(CLIENT_ERROR, cause, "foo", 123);

		assertAll(() -> assertEquals(CLIENT_ERROR, exception.getCode()),
				() -> assertArrayEquals(new Object[] { "foo", 123 }, exception.getArgs()),
				() -> assertSame(cause, exception.getCause()),
				() -> assertEquals("CLIENT_ERROR: [foo, 123]", exception.getMessage()));
	}

	@Test
	void shouldHandleExplicitNullArgsArray() {
		ClientException exception = new ClientException(CLIENT_ERROR, (Throwable) null, (Object[]) null);

		assertAll(() -> assertEquals(CLIENT_ERROR, exception.getCode()),
				() -> assertArrayEquals(new Object[0], exception.getArgs()),
				() -> assertEquals("CLIENT_ERROR: null", exception.getMessage()),
				() -> assertNull(exception.getCause()));
	}

	@Test
	void shouldDefensivelyCopyArgsInConstructor() {
		Object[] args = { "foo", 123 };
		ClientException exception = new ClientException(ERROR, args);
		args[0] = "changed";
		args[1] = 999;

		assertArrayEquals(new Object[] { "foo", 123 }, exception.getArgs());
	}

	@Test
	void shouldDefensivelyCopyArgsInGetter() {
		ClientException exception = new ClientException(ERROR, "foo", 123);
		Object[] args = exception.getArgs();
		args[0] = "changed";
		args[1] = 999;

		assertArrayEquals(new Object[] { "foo", 123 }, exception.getArgs());
	}

	@Test
	void shouldBeEqualWhenCodeAndArgsAreEqual() {
		ClientException first = new ClientException(ERROR, "foo", 123);
		ClientException second = new ClientException(ERROR, "foo", 123);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldBeEqualWhenArgsAreSeparateArrays() {
		ClientException first = new ClientException(ERROR, new Object[] { "foo", 123 });
		ClientException second = new ClientException(ERROR, new Object[] { "foo", 123 });

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldNotBeEqualWhenCodeDiffers() {
		ClientException first = new ClientException("ERROR_ONE", "foo");
		ClientException second = new ClientException("ERROR_TWO", "foo");

		assertNotEquals(first, second);
	}

	@Test
	void shouldNotBeEqualWhenArgsDiffer() {
		ClientException first = new ClientException(ERROR, "foo");
		ClientException second = new ClientException(ERROR, "bar");

		assertNotEquals(first, second);
	}

	@Test
	void shouldNotBeEqualWhenNumberOfArgsDiffers() {
		ClientException first = new ClientException(ERROR, "foo");
		ClientException second = new ClientException(ERROR, "foo", "bar");

		assertNotEquals(first, second);
	}

	@Test
	void shouldBeEqualWhenBothHaveNoArgs() {
		ClientException first = new ClientException(ERROR);
		ClientException second = new ClientException(ERROR);

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldBeEqualToItself() {
		ClientException exception = new ClientException(ERROR, "foo");
		assertEquals(exception, exception);
	}

	@Test
	void shouldNotBeEqualToNull() {
		ClientException exception = new ClientException(ERROR);
		assertNotNull(exception);
	}

	@Test
	void shouldNotBeEqualToDifferentType() {
		ClientException exception = new ClientException(ERROR);
		assertNotEquals(ERROR, exception);
	}

	@Test
	void shouldIgnoreCauseForEquality() {
		Throwable firstCause = new IOException("first");
		Throwable secondCause = new IOException("second");
		ClientException first = new ClientException(ERROR, firstCause, "foo");
		ClientException second = new ClientException(ERROR, secondCause, "foo");

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void shouldUseCodeAndArgsInMessage() {
		ClientException exception = new ClientException("VALIDATION_ERROR", "name", "required");
		assertEquals("VALIDATION_ERROR: [name, required]", exception.getMessage());
	}
}