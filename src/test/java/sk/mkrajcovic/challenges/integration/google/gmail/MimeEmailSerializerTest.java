package sk.mkrajcovic.challenges.integration.google.gmail;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Base64;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.mail.Email;

class MimeEmailSerializerTest {

	private final MimeEmailSerializer serializer = new MimeEmailSerializer();

	@Test
	void serializesPlainTextEmailToGmailRawMessage() {
		var email = Email.builder()
			.to("to@example.com")
			.to("second-to@example.com")
			.cc("cc@example.com")
			.bcc("bcc@example.com")
			.subject("Čau")
			.textBody("Hello world")
			.build();

		String rawMessage = serializer.serialize("from@example.com", email);

		assertEquals("""
				From: from@example.com\r
				To: to@example.com, second-to@example.com\r
				Cc: cc@example.com\r
				Bcc: bcc@example.com\r
				Subject: =?UTF-8?B?xIxhdQ==?=\r
				MIME-Version: 1.0\r
				Content-Type: text/plain; charset=UTF-8\r
				Content-Transfer-Encoding: base64\r
				\r
				SGVsbG8gd29ybGQ=\r
				""", decodeRawMessage(rawMessage));
	}

	@Test
	void omitsEmptyCcAndBccHeaders() {
		var email = Email.builder()
			.to("to@example.com")
			.subject("Subject")
			.textBody("Body")
			.build();

		String rawMessage = serializer.serialize("from@example.com", email);

		assertEquals("""
				From: from@example.com\r
				To: to@example.com\r
				Subject: =?UTF-8?B?U3ViamVjdA==?=\r
				MIME-Version: 1.0\r
				Content-Type: text/plain; charset=UTF-8\r
				Content-Transfer-Encoding: base64\r
				\r
				Qm9keQ==\r
				""", decodeRawMessage(rawMessage));
	}

	@Test
	void wrapsBase64BodyAtMimeLineLength() {
		var email = Email.builder()
			.to("to@example.com")
			.subject("Subject")
			.textBody("a".repeat(58))
			.build();

		String rawMessage = serializer.serialize("from@example.com", email);

		assertEquals("""
				From: from@example.com\r
				To: to@example.com\r
				Subject: =?UTF-8?B?U3ViamVjdA==?=\r
				MIME-Version: 1.0\r
				Content-Type: text/plain; charset=UTF-8\r
				Content-Transfer-Encoding: base64\r
				\r
				YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFh\r
				YQ==\r
				""", decodeRawMessage(rawMessage));
	}

	private static String decodeRawMessage(String rawMessage) {
		return new String(Base64.getUrlDecoder().decode(rawMessage), UTF_8);
	}
}
