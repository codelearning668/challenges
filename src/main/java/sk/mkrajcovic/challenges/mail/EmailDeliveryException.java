package sk.mkrajcovic.challenges.mail;

/**
 * Signals that an email provider could not deliver a message.
 */
@SuppressWarnings("serial")
public class EmailDeliveryException extends RuntimeException {

	public EmailDeliveryException(String message) {
		super(message);
	}

	public EmailDeliveryException(String message, Throwable cause) {
		super(message, cause);
	}
}
