package sk.mkrajcovic.challenges.mail;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import sk.mkrajcovic.challenges.util.Text;

/**
 * Application-level plain-text email message, independent of a delivery provider.
 * <p>
 * The provider integration turns this message into its required format.
 */
@Getter
public final class Email {

	private final List<String> to;
	private final List<String> cc;
	private final List<String> bcc;
	private final String subject;
	private final String textBody;

	private Email(
			List<String> to,
			List<String> cc,
			List<String> bcc,
			String subject,
			String textBody) {

		verifyAtLeastOneRecipientIsDefined(to, cc, bcc);
		verifyTextBodyIsDefined(textBody);

		verifyHeaderValueHasNoLineBreaks(subject, "subject");
		to.forEach(address -> verifyHeaderValueHasNoLineBreaks(address, "to recipient"));
		cc.forEach(address -> verifyHeaderValueHasNoLineBreaks(address, "cc recipient"));
		bcc.forEach(address -> verifyHeaderValueHasNoLineBreaks(address, "bcc recipient"));

		this.to = List.copyOf(to);
		this.cc = List.copyOf(cc);
		this.bcc = List.copyOf(bcc);
		this.subject = subject;
		this.textBody = textBody;
	}

	/**
	 * Email instances can only be created through this builder.
	 *
	 * @return empty email builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	private static void verifyAtLeastOneRecipientIsDefined(List<String> to, List<String> cc, List<String> bcc) {
		if (to.isEmpty() && cc.isEmpty() && bcc.isEmpty()) {
			throw new IllegalArgumentException("Email needs at least one recipient");
		}
	}

	private static void verifyHeaderValueHasNoLineBreaks(String value, String name) {
		if (Text.isBlank(value) || value.indexOf('\r') >= 0 || value.indexOf('\n') >= 0) {
			throw new IllegalArgumentException("Invalid " + name);
		}
	}

	private static void verifyTextBodyIsDefined(String textBody) {
		if (Text.isBlank(textBody)) {
			throw new IllegalArgumentException("Email text body must not be blank");
		}
	}

	public static final class Builder {

		private final List<String> to = new ArrayList<>();
		private final List<String> cc = new ArrayList<>();
		private final List<String> bcc = new ArrayList<>();
		private String subject;
		private String textBody;

		public Builder to(String recipient) {
			to.add(recipient);
			return this;
		}

		public Builder cc(String recipient) {
			cc.add(recipient);
			return this;
		}

		public Builder bcc(String recipient) {
			bcc.add(recipient);
			return this;
		}

		public Builder subject(String subject) {
			this.subject = subject;
			return this;
		}

		public Builder textBody(String textBody) {
			this.textBody = textBody;
			return this;
		}

		public Email build() {
			return new Email(to, cc, bcc, subject, textBody);
		}
	}
}
