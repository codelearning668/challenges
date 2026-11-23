package sk.mkrajcovic.challenges.mail;

/**
 * Delivers application email messages.
 * <p>
 * Separates application email use cases from a provider-specific integration.
 */
public interface EmailSender {

	/**
	 * Delivers the supplied email.
	 *
	 * @param email message to deliver
	 */
	void send(Email email);
}
