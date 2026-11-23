package sk.mkrajcovic.challenges.integration.google.gmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GmailConfigTest {

	@Test
	void retainsConfiguredValues() {
		var config = new GmailConfig(
			"client-id",
			"client-secret",
			"refresh-token",
			"from@example.com");

		assertEquals("client-id", config.getClientId());
		assertEquals("client-secret", config.getClientSecret());
		assertEquals("refresh-token", config.getRefreshToken());
		assertEquals("from@example.com", config.getFrom());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void rejectsBlankClientId(String blankValue) {
		assertThrows(IllegalStateException.class, () -> new GmailConfig(
			blankValue,
			"client-secret",
			"refresh-token",
			"from@example.com"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void rejectsBlankClientSecret(String blankValue) {
		assertThrows(IllegalStateException.class, () -> new GmailConfig(
			"client-id",
			blankValue,
			"refresh-token",
			"from@example.com"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void rejectsBlankRefreshToken(String blankValue) {
		assertThrows(IllegalStateException.class, () -> new GmailConfig(
			"client-id",
			"client-secret",
			blankValue,
			"from@example.com"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = { " ", "\t", "\n" })
	void rejectsBlankSenderAddress(String blankValue) {
		assertThrows(IllegalStateException.class, () -> new GmailConfig(
			"client-id",
			"client-secret",
			"refresh-token",
			blankValue));
	}
}
