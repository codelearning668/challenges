package sk.mkrajcovic.challenges.integration.google.gmail;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.mail.EmailDeliveryException;

/**
 * Obtains Gmail API access tokens from the configured refresh token.
 * <p>
 * Refresh-token exchange keeps the short-lived access token out of application
 * configuration.
 */
@Component
@RequiredArgsConstructor
public class GmailAccessTokenProvider {

	private static final Logger LOG = LoggerFactory.getLogger(GmailAccessTokenProvider.class);

	private static final URI TOKEN_URI = URI.create("https://oauth2.googleapis.com/token");
	private static final String ACCESS_TOKEN_JSON_PROPERTY = "access_token";
	private static final Pattern ACCESS_TOKEN_JSON_PROPERTY_PATTERN = Pattern.compile(
			"\\\"" + Pattern.quote(ACCESS_TOKEN_JSON_PROPERTY) + "\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

	private final GmailConfig gmailConfig;
	private final GmailHttpClient gmailHttpClient;

	/**
	 * Exchanges the configured refresh token for a Gmail API access token.
	 *
	 * @return current access token
	 */
	public String getAccessToken() {
		String refreshTokenFormPayload = createRefreshTokenFormPayload();
		var httpRequest = createAccessTokenRequest(refreshTokenFormPayload);

		LOG.debug("Requesting Gmail OAuth access token");
		var httpResponse = gmailHttpClient.send(httpRequest);
		handleError(httpResponse);

		String accessToken = extractAccessTokenFromJson(httpResponse.body());
		LOG.debug("Obtained Gmail OAuth access token");

		return accessToken;
	}

	/*
	 * application/x-www-form-urlencoded requires one continuous, URL-encoded body.
	 * Do not use a text block: its newlines/indentation would become request-body
	 * characters and could make Google parse a form parameter incorrectly.
	 */
	private String createRefreshTokenFormPayload() {
		return "client_id=" + urlEncode(gmailConfig.getClientId())
			+ "&client_secret=" + urlEncode(gmailConfig.getClientSecret())
			+ "&refresh_token=" + urlEncode(gmailConfig.getRefreshToken())
			+ "&grant_type=refresh_token";
	}

	private String urlEncode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private HttpRequest createAccessTokenRequest(String form) {
		return HttpRequest.newBuilder(TOKEN_URI)
			.timeout(Duration.ofSeconds(30))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
			.build();
	}

	private void handleError(HttpResponse<String> response) {
		int statusCode = response.statusCode();
		if (statusCode / 100 != 2) {
			throw new EmailDeliveryException("Google OAuth request failed: HTTP " + statusCode + " -> " + response.body());
		}
	}

	private String extractAccessTokenFromJson(String json) {
		var matcher = ACCESS_TOKEN_JSON_PROPERTY_PATTERN.matcher(json);
		if (!matcher.find()) {
			throw new EmailDeliveryException("Google OAuth response did not contain " + ACCESS_TOKEN_JSON_PROPERTY);
		}
		return matcher.group(1);
	}
}
