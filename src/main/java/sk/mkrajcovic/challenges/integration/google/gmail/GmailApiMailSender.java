package sk.mkrajcovic.challenges.integration.google.gmail;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.mail.Email;
import sk.mkrajcovic.challenges.mail.EmailDeliveryException;
import sk.mkrajcovic.challenges.mail.EmailSender;

/**
 * Delivers application email messages through the Gmail API.
 */
@Component
@RequiredArgsConstructor
public class GmailApiMailSender implements EmailSender {

	private static final Logger LOG = LoggerFactory.getLogger(GmailApiMailSender.class);

	private static final URI SEND_URI = URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send");

	private final GmailAccessTokenProvider accessTokenProvider;
	private final GmailConfig gmailConfig;
	private final MimeEmailSerializer mimeEmailSerializer;
	private final GmailHttpClient gmailHttpClient;

	/**
	 * Sends an email through Gmail.
	 *
	 * @param email message to send
	 */
	@Override
	public void send(Email email) {
		String rawMessage = mimeEmailSerializer.serialize(gmailConfig.getFrom(), email);
		var httpRequest = createRequest(rawMessage);
		var httpResponse = gmailHttpClient.send(httpRequest);
		verifyGmailAcceptedEmail(httpResponse);

		logEmailAcceptedForDelivery(email);
	}

	private HttpRequest createRequest(String rawMessage) {
		return HttpRequest.newBuilder(SEND_URI)
			.timeout(Duration.ofSeconds(30))
			.header("Authorization", "Bearer " + accessTokenProvider.getAccessToken())
			.header("Content-Type", "application/json; charset=UTF-8")
			.POST(HttpRequest.BodyPublishers.ofString(createRequestBody(rawMessage), UTF_8))
			.build();
	}

    private String createRequestBody(String rawMessage) {
        return "{\"raw\":\"" + rawMessage + "\"}";
    }

	private void verifyGmailAcceptedEmail(HttpResponse<String> response) {
		int statusCode = response.statusCode();
		if (statusCode / 100 != 2) {
			throw new EmailDeliveryException("Gmail API rejected the email: HTTP " + statusCode);
		}
	}

	private void logEmailAcceptedForDelivery(Email email) {
		LOG.info("Gmail accepted email for delivery; recipientCount={}, ccRecipientCount={}, bccRecipientCount={}",
			email.getTo().size(),
			email.getCc().size(),
			email.getBcc().size()
		);
	}

}
