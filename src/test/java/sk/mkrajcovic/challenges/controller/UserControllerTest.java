package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.BAD_REQUEST;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CONFLICT;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NO_CONTENT;
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
import sk.mkrajcovic.challenges.controller.dto.UserEmailAddressUpdateRequest;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.test.util.HttpCodes;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

	private static final String USER_REGISTRATION_URI = "/users/register";
	private static final String USER_EMAIL_ADDRESS_URI = "/users/email-address";

	private static final String VALID_USERNAME = "TestUser";
	private static final String VALID_PASSWORD = "TestUserPwd";
	private static final String VALID_EMAIL_ADDRESS = "test.user@example.com";
	private static final String UPDATED_EMAIL_ADDRESS = "updated.user@example.com";
	private static final String TAKEN_USERNAME = "TestUserTaken"; // same user registered twice
	private static final String WHITESPACE = " ";
	private static final String INVALID_EMAIL_ADDRESS = "not-an-email";
	private static final String EMAIL_ADDRESS_TOO_LONG = "x".repeat(40) + "@example.com";
	private static final String USERNAME_TOO_LONG = "x".repeat(101);
	private static final String PASSWORD_TOO_LONG = "x".repeat(501);

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Nested
	class UserRegistrationTest {

		@Nested
		class Positive {
			@Test
			void anyoneCanRegister() {
				register(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL_ADDRESS)
					.then()
						.statusCode(HttpCodes.CREATED);

				var registeredUser = userRepository.findById(VALID_USERNAME).orElseThrow();
				assertEquals(VALID_EMAIL_ADDRESS, registeredUser.getEmailAddress());
			}

			@Test
			void anyoneCanRegisterWithoutEmailAddress() {
				String username = "TestUserWithoutEmail";

				register(username, VALID_PASSWORD, null)
					.then()
						.statusCode(HttpCodes.CREATED);

				var registeredUser = userRepository.findById(username).orElseThrow();
				assertNull(registeredUser.getEmailAddress());
			}
		}

		@Nested
		class Negative {

			@Test
			void cannotRegisterWhenUserNameTaken() {
				register(TAKEN_USERNAME, VALID_PASSWORD);
				register(TAKEN_USERNAME, "different-pwd")
					.then()
						.statusCode(CONFLICT);
			}

			@Nested
			class ValidationErrors {

				@Test
				void withoutName() {
					register(null, VALID_PASSWORD)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyName() {
					register(WHITESPACE, VALID_PASSWORD)
						.then()
						.statusCode(BAD_REQUEST);
				}

				@Test
				void withNameTooLong() {
					register(USERNAME_TOO_LONG, VALID_PASSWORD)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withoutPassword() {
					register(VALID_USERNAME, null)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyPassword() {
					register(VALID_USERNAME, WHITESPACE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withPasswordTooLong() {
					register(VALID_USERNAME, PASSWORD_TOO_LONG)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withInvalidEmailAddress() {
					register(VALID_USERNAME, VALID_PASSWORD, INVALID_EMAIL_ADDRESS)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withBlankEmailAddress() {
					register(VALID_USERNAME, VALID_PASSWORD, WHITESPACE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmailAddressTooLong() {
					register(VALID_USERNAME, VALID_PASSWORD, EMAIL_ADDRESS_TOO_LONG)
						.then()
							.statusCode(BAD_REQUEST);
				}
			}
		}
	}

	@Nested
	class UserEmailAddressUpdateTest {

		@Test
		void authenticatedUserCanUpdateOwnEmailAddress() {
			String username = registerUserForEmailUpdate(VALID_EMAIL_ADDRESS);

			updateEmailAddress(username, VALID_PASSWORD, UPDATED_EMAIL_ADDRESS)
				.then()
					.statusCode(NO_CONTENT);

			var updatedUser = userRepository.findById(username).orElseThrow();
			assertEquals(UPDATED_EMAIL_ADDRESS, updatedUser.getEmailAddress());
		}

		@Test
		void authenticatedUserCanRemoveOwnEmailAddress() {
			String username = registerUserForEmailUpdate(VALID_EMAIL_ADDRESS);

			updateEmailAddress(username, VALID_PASSWORD, null)
				.then()
					.statusCode(NO_CONTENT);

			var updatedUser = userRepository.findById(username).orElseThrow();
			assertNull(updatedUser.getEmailAddress());
		}

		@Test
		void unauthenticatedUserCannotUpdateEmailAddress() {
			given()
				.contentType(ContentType.JSON)
				.body(new UserEmailAddressUpdateRequest(UPDATED_EMAIL_ADDRESS))
			.when()
				.patch(USER_EMAIL_ADDRESS_URI)
			.then()
				.statusCode(UNAUTHORIZED);
		}

		@Test
		void rejectsInvalidEmailAddress() {
			String username = registerUserForEmailUpdate(null);

			updateEmailAddress(username, VALID_PASSWORD, INVALID_EMAIL_ADDRESS)
				.then()
					.statusCode(BAD_REQUEST);
		}

		@Test
		void rejectsBlankEmailAddress() {
			String username = registerUserForEmailUpdate(null);

			updateEmailAddress(username, VALID_PASSWORD, WHITESPACE)
				.then()
					.statusCode(BAD_REQUEST);
		}

		@Test
		void rejectsEmailAddressTooLong() {
			String username = registerUserForEmailUpdate(null);

			updateEmailAddress(username, VALID_PASSWORD, EMAIL_ADDRESS_TOO_LONG)
				.then()
					.statusCode(BAD_REQUEST);
		}
	}

	Response register(String username, String password) {
		return register(username, password, null);
	}

	Response register(String username, String password, String emailAddress) {
		return given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(new UserRegistrationRequest(username, password, emailAddress))
			.when()
				.post(USER_REGISTRATION_URI);
	}

	private String registerUserForEmailUpdate(String emailAddress) {
		String username = "email-update-" + UUID.randomUUID();
		register(username, VALID_PASSWORD, emailAddress)
			.then()
				.statusCode(HttpCodes.CREATED);
		return username;
	}

	private Response updateEmailAddress(String username, String password, String emailAddress) {
		return given()
				.auth().preemptive().basic(username, password)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(new UserEmailAddressUpdateRequest(emailAddress))
			.when()
				.patch(USER_EMAIL_ADDRESS_URI);
	}

}
