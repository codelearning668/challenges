package sk.mkrajcovic.challenges.integration.google.gmail;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.stereotype.Component;

import sk.mkrajcovic.challenges.mail.EmailDeliveryException;

/**
 * Executes HTTP requests required by the Gmail integration.
 * <p>
 * Reuses one JDK HTTP client for OAuth token and Gmail API requests.
 */
@Component
public class GmailHttpClient {

	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.build();

	/**
	 * Sends an HTTP request and returns its text response.
	 *
	 * @param request request to send
	 * @return response returned by Google
	 */
	public HttpResponse<String> send(HttpRequest request) {
		try {
			return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		} catch (IOException ioex) {
			throw new EmailDeliveryException("Google request failed", ioex);
		} catch (InterruptedException iex) {
			/*
			 * HttpClient.send() clears the current thread's interrupted status before
			 * throwing InterruptedException. We restore it so the caller, executor, or
			 * shutdown flow can still detect and respect the original interruption request.
			 */
			Thread.currentThread().interrupt();
			throw new EmailDeliveryException("Google request was interrupted", iex);
		}
	}
}
