package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

	private static final String VALID_USERNAME = "TestUser";
	private static final String VALID_PASSWORD = "TestUserPwd";
	private static final String TAKEN_USERNAME = "TestUserTaken"; // same user registered twice

	private static final String WHITESPACE = " ";
	private static final String USERNAME_TOO_LONG = "x".repeat(101);
	private static final String PASSWORD_TOO_LONG = "x".repeat(501);

	private static final int OK = HttpStatus.OK.value();
	private static final int CONFLICT = HttpStatus.CONFLICT.value();
	private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Nested
	class UserRegistrationTest {

		Response register(String username, String password) {
			return given()
					.contentType(ContentType.JSON)
					.accept(ContentType.JSON)
					.body(new UserRegistrationRequest(username, password))
				.when()
					.post("/users/register");
		}

		@Nested
		class Positive {
			@Test
			void anyoneCanRegister() {
				register(VALID_USERNAME, VALID_PASSWORD)
					.then()
						.statusCode(OK);
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
			}
		}

	}

}
