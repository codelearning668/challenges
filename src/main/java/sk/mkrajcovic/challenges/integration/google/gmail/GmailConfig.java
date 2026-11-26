package sk.mkrajcovic.challenges.integration.google.gmail;

import static sk.mkrajcovic.challenges.util.Text.isAnyBlank;

import lombok.Getter;

/**
 * Provides validated configuration required by the Gmail API integration.
 * <p>
 * Fails during application startup instead of on the first delivery attempt
 * when the integration is not configured.
 */
@Getter
public class GmailConfig {

	private final String clientId;
	private final String clientSecret;
	private final String refreshToken;
	private final String from;

	public GmailConfig(String clientId, String clientSecret, String refreshToken, String from) {

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
