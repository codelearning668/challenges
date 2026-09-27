package sk.mkrajcovic.challenges.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ValueTest {

	@Test
	void firstNonNullReturnsFirstValueWhenBothValuesAreProvided() {
		var first = "first";
		var second = "second";

		var result = Value.firstNonNull(first, second);
		assertEquals(first, result);
	}

	@Test
	void firstNonNullReturnsSecondValueWhenFirstValueIsNull() {
		var first = (String) null;
		var second = "second";

		var result = Value.firstNonNull(first, second);
		assertEquals(second, result);
	}

	@Test
	void firstNonNullReturnsNullWhenBothValuesAreNull() {
		var first = (String) null;
		var second = (String) null;

		var result = Value.firstNonNull(first, second);
		assertNull(result);
	}

}
