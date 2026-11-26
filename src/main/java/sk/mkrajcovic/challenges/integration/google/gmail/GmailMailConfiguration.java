package sk.mkrajcovic.challenges.integration.google.gmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sk.mkrajcovic.challenges.mail.EmailSender;

/**
 * Creates the Gmail delivery integration only when Gmail is the configured
 * application mail provider.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "challenges.mail", name = "provider", havingValue = "gmail")
public class GmailMailConfiguration {

	@Bean
	GmailConfig gmailConfig(
			@Value("${challenges.gmail.client-id:}") String clientId,
			@Value("${challenges.gmail.client-secret:}") String clientSecret,
			@Value("${challenges.gmail.refresh-token:}") String refreshToken,
			@Value("${challenges.gmail.from:}") String from) {

		return new GmailConfig(clientId, clientSecret, refreshToken, from);
	}

	@Bean
	GmailHttpClient gmailHttpClient() {
		return new GmailHttpClient();
	}

	@Bean
	MimeEmailSerializer mimeEmailSerializer() {
		return new MimeEmailSerializer();
	}

	@Bean
	GmailAccessTokenProvider gmailAccessTokenProvider(GmailConfig gmailConfig, GmailHttpClient gmailHttpClient) {
		return new GmailAccessTokenProvider(gmailConfig, gmailHttpClient);
	}

	@Bean
	EmailSender gmailApiMailSender(
			GmailAccessTokenProvider accessTokenProvider,
			GmailConfig gmailConfig,
			MimeEmailSerializer mimeEmailSerializer,
			GmailHttpClient gmailHttpClient) {

		return new GmailApiMailSender(accessTokenProvider, gmailConfig, mimeEmailSerializer, gmailHttpClient);
	}
}