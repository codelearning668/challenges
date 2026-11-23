package sk.mkrajcovic.challenges.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.mail.Email;
import sk.mkrajcovic.challenges.mail.EmailSender;

/**
 * Coordinates application email delivery through the {@link EmailSender}
 * boundary.
 * <p>
 * Business use cases can depend on this service without knowing the delivery
 * provider.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

	private final EmailSender emailSender;

	/**
	 * Delegates email delivery to the configured provider.
	 *
	 * @param email message to send
	 */
	public void send(Email email) {
		emailSender.send(email);
	}
}
