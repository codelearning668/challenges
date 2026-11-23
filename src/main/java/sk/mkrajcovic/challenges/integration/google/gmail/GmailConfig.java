package sk.mkrajcovic.challenges.integration.google.gmail;

import static sk.mkrajcovic.challenges.util.Text.isAnyBlank;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * Provides validated configuration required by the Gmail API integration.
 * <p>
 * Fails during application startup instead of on the first delivery attempt
 * when the integration is not configured.
 */
@Component
@Getter
public class GmailConfig {

	private final String clientId;
	private final String clientSecret;
	private final String refreshToken;
	private final String from;

	public GmailConfig(
			@Value("${challenges.gmail.client-id:}") String clientId,
			@Value("${challenges.gmail.client-secret:}") String clientSecret,
			@Value("${challenges.gmail.refresh-token:}") String refreshToken,
			@Value("${challenges.gmail.from:}") String from) {

		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;
		this.from = from;

		verifyMandatoryPropertiesAreDefined();
	}

	private void verifyMandatoryPropertiesAreDefined() {
		if (isAnyBlank(clientId, clientSecret, refreshToken, from)) {
			throw new IllegalStateException(
				"Configure challenges.gmail.client-id, client-secret, refresh-token and from");
		}
	}
}
