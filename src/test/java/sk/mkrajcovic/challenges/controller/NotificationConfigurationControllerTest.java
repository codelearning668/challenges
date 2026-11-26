package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NO_CONTENT;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.OK;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNAUTHORIZED;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationUpdateRequest;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;
import sk.mkrajcovic.challenges.repository.persistence.NotificationConfigurationRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationConfigurationControllerTest {

	private static final String NOTIFICATION_CONFIGURATION_URI = "/notification-configuration";
	private static final String USER_REGISTRATION_URI = "/users/register";
	private static final String PASSWORD = "NotificationConfigurationPwd";

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@Autowired
	private NotificationConfigurationRepository notificationConfigurationRepository;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Nested
	class GetNotificationConfigurationTest {

		@Test
		void authenticatedUserCanGetOwnDefaultNotificationConfiguration() {
			var username = registerUser();

			getNotificationConfiguration(username)
				.then()
					.statusCode(OK)
					.contentType(ContentType.JSON)
					.body("notifyOnChallengeCreated", equalTo(true))
					.body("notifyOnChallengeClosed", equalTo(false));
		}

		@Test
		void unauthenticatedUserCannotGetNotificationConfiguration() {
			given()
				.accept(ContentType.JSON)
			.when()
				.get(NOTIFICATION_CONFIGURATION_URI)
			.then()
				.statusCode(UNAUTHORIZED);
		}
	}

	@Nested
	class UpdateNotificationConfigurationTest {

		@Test
		void authenticatedUserCanUpdateOwnNotificationConfiguration() {
			var username = registerUser();
			var request = new NotificationConfigurationUpdateRequest(false, true);

			updateNotificationConfiguration(username, request)
				.then()
					.statusCode(NO_CONTENT);

			getNotificationConfiguration(username)
				.then()
					.statusCode(OK)
					.body("notifyOnChallengeCreated", equalTo(false))
					.body("notifyOnChallengeClosed", equalTo(true));
		}

		@Test
		void unauthenticatedUserCannotUpdateNotificationConfiguration() {
			given()
				.contentType(ContentType.JSON)
				.body(new NotificationConfigurationUpdateRequest(false, true))
			.when()
				.put(NOTIFICATION_CONFIGURATION_URI)
			.then()
				.statusCode(UNAUTHORIZED);
		}
	}

	private String registerUser() {
		var username = "notification-configuration-" + UUID.randomUUID();

		given()
				.contentType(ContentType.JSON)
				.body(new UserRegistrationRequest(username, PASSWORD, null))
			.when()
				.post(USER_REGISTRATION_URI)
			.then()
				.statusCode(201);

		/*
		 * Registration must create the configuration in the same transaction as
		 * the user. This assertion ensures the endpoint tests do not pass merely
		 * because an unrelated fallback configuration was created later.
		 */
		notificationConfigurationRepository.findByUserUsername(username).orElseThrow();

		return username;
	}

	private Response getNotificationConfiguration(String username) {
		return given()
				.auth().preemptive().basic(username, PASSWORD)
				.accept(ContentType.JSON)
			.when()
				.get(NOTIFICATION_CONFIGURATION_URI);
	}

	private Response updateNotificationConfiguration(
			String username,
			NotificationConfigurationUpdateRequest request) {

		return given()
				.auth().preemptive().basic(username, PASSWORD)
				.contentType(ContentType.JSON)
				.body(request)
			.when()
				.put(NOTIFICATION_CONFIGURATION_URI);
	}
}
