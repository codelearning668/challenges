package sk.mkrajcovic.challenges.integration.google.gmail;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import java.util.List;

import org.springframework.stereotype.Component;

import sk.mkrajcovic.challenges.mail.Email;

/**
 * Serializes application email messages to Gmail's RFC 822 raw-message format.
 */
@Component
public class MimeEmailSerializer {

	/** MIME requires Base64-encoded body lines to contain max 76 characters */
	private static final int MIME_BASE64_LINE_LENGTH = 76;

	/**
	 * Serializes an email as a Gmail API raw message.
	 * <p>
	 * Gmail expects the complete RFC 822 message as
	 * URL-safe Base64 without padding.
	 *
	 * @param from  configured sender address
	 * @param email message to serialize
	 * @return URL-safe Base64 encoded RFC 822 message
	 */
	public String serialize(String from, Email email) {
		byte[] mimeMessage = createMimeMessageBytes(from, email);
		return Base64.getUrlEncoder()
			.withoutPadding()
			.encodeToString(mimeMessage);
	}

	/*
	 * Uses CRLF line endings because RFC 822/MIME message headers
	 * and body separators require them;
	 */
	private byte[] createMimeMessageBytes(String from, Email email) {
		String mimeMessage = "From: " + from + "\r\n"
			+ createRecipientsHeader("To", email.getTo())
			+ createRecipientsHeader("Cc", email.getCc())
			+ createRecipientsHeader("Bcc", email.getBcc())
			+ "Subject: " + encodeHeader(email.getSubject()) + "\r\n"
			+ "MIME-Version: 1.0\r\n"
			+ "Content-Type: text/plain; charset=UTF-8\r\n"
			+ "Content-Transfer-Encoding: base64\r\n"
			+ "\r\n"
			+ Base64.getMimeEncoder(MIME_BASE64_LINE_LENGTH, "\r\n".getBytes(US_ASCII))
				.encodeToString(email.getTextBody().getBytes(UTF_8))
			+ "\r\n";

		return mimeMessage.getBytes(UTF_8);
	}

	private String createRecipientsHeader(String name, List<String> recipients) {
		return recipients.isEmpty() ? "" : name + ": " + String.join(", ", recipients) + "\r\n";
	}

	private String encodeHeader(String value) {
		return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(value.getBytes(UTF_8)) + "?=";
	}

}
