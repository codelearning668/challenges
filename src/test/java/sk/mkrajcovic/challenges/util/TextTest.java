package sk.mkrajcovic.challenges.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TextTest {

	@Nested
	class NormalizeForSearch {

		@Test
		void nullReturnsNull() {
			assertNull(Text.normalizeForSearch(null));
		}

		@Test
		void lowercasesText() {
			assertEquals("lowercase word", Text.normalizeForSearch("LoWeRcAsE WORD"));
		}

		@Test
		void removesDiacritics() {
			assertEquals("stefania", Text.normalizeForSearch("Štefánia"));
		}

		@Test
		void lowercasesAndRemovesDiacritics() {
			assertEquals("cesky pivovar", Text.normalizeForSearch("ČESKÝ PIVOVAR"));
		}

		@Test
		void preservesNonDiacriticCharacters() {
			assertEquals("highlands - long (12km)", Text.normalizeForSearch("Highlands - Long (12km)"));
		}

		@Test
		void handlesEmptyString() {
			assertEquals("", Text.normalizeForSearch(""));
		}

		@Test
		void removesCombiningDiacritics() {
			assertEquals("e", Text.normalizeForSearch("e\u0301"));
		}
	}
}
