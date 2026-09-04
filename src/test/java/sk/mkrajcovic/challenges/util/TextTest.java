package sk.mkrajcovic.challenges.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TextTest {

	@Nested
	@DisplayName("isBlank()")
	class IsBlankTest {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " ", "   ", "\t", "\n" })
		void shouldReturnTrueForBlankInputs(String input) {
			assertTrue(Text.isBlank(input));
		}

		@ParameterizedTest
		@ValueSource(strings = { "a", "abc", " abc ", "0" })
		void shouldReturnFalseForNonBlankInputs(String input) {
			assertFalse(Text.isBlank(input));
		}
	}

	@Nested
	@DisplayName("isNotBlank()")
	class IsNotBlankTest {

		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = { " ", "   ", "\t", "\n" })
		void shouldReturnFalseForBlankInputs(String input) {
			assertFalse(Text.isNotBlank(input));
		}

		@ParameterizedTest
		@ValueSource(strings = { "a", "abc", " abc ", "0" })
		void shouldReturnTrueForNonBlankInputs(String input) {
			assertTrue(Text.isNotBlank(input));
		}
	}

	@Nested
	@DisplayName("normalizeForSearch()")
	class NormalizeForSearchTest {

		@ParameterizedTest
		@CsvSource(textBlock = """
			'LoWeRcAsE WORD', 'lowercase word'
			'Štefánia', 'stefania'
			'ČESKÝ PIVOVAR', 'cesky pivovar'
			'Highlands - Long (12km)', 'highlands - long (12km)'
			'', ''
		""")
		void normalizesText(String input, String expected) {
			assertEquals(expected, Text.normalizeForSearch(input));
		}

		@Test
		void nullReturnsNull() {
			assertNull(Text.normalizeForSearch(null));
		}

		@Test
		void removesCombiningDiacritics() {
			// verify that diacritics represented as combining Unicode characters are removed
			assertEquals("e", Text.normalizeForSearch("e\u0301"));
		}
	}

}
