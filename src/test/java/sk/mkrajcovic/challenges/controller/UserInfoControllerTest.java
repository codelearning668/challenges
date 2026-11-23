package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.restassured.http.ContentType;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.security.UserRoles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserInfoControllerTest {

	private static final String USER_INFO_URI = "/users/info";

	private static final String VALID_USERNAME = "ParticipantUser";
	private static final String VALID_PASSWORD = "ParticipantUserPwd";
	private static final String VALID_EMAIL_ADDRESS = "participant.user@example.com";
	private static final String USER_WITHOUT_EMAIL_ADDRESS = "ParticipantWithoutEmail";
	private static final String USER_WITHOUT_EMAIL_ADDRESS_PASSWORD = "ParticipantWithoutEmailPwd";

	@LocalServerPort
	int port;

	@Autowired private InitHelper helper;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private UserRepository userRepository;

	@BeforeAll
	void createUsers() {
		userRepository.save(createParticipant(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL_ADDRESS));
		userRepository.save(createParticipant(USER_WITHOUT_EMAIL_ADDRESS, USER_WITHOUT_EMAIL_ADDRESS_PASSWORD, null));
	}

	private User createParticipant(String username, String password, String emailAddress) {
		var participant = new User();
		participant.setUsername(username);
		participant.setPassword(passwordEncoder.encode(password));
		participant.setEmailAddress(emailAddress);
		participant.addAuthority(UserRoles.PARTICIPANT);
		participant.setEnabled(true);
		return participant;
	}

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Nested
	class UserInfoTest {

		@Test
		void authenticatedUserCanGetOwnInfo() {
			getUserInfo(VALID_USERNAME, VALID_PASSWORD)
			.then()
				.statusCode(200)
				.body("username", equalTo(VALID_USERNAME))
				.body("emailAddress", equalTo(VALID_EMAIL_ADDRESS))
				.body("roles", contains(UserRoles.PARTICIPANT));
		}

		@Test
		void authenticatedUserWithoutEmailAddressGetsNullEmailAddress() {
			getUserInfo(USER_WITHOUT_EMAIL_ADDRESS, USER_WITHOUT_EMAIL_ADDRESS_PASSWORD)
			.then()
				.statusCode(200)
				.body("username", equalTo(USER_WITHOUT_EMAIL_ADDRESS))
				.body("emailAddress", nullValue())
				.body("roles", contains(UserRoles.PARTICIPANT));
		}

		private io.restassured.response.Response getUserInfo(String username, String password) {
			return given()
				.auth()
					.preemptive()
					.basic(username, password)
				.accept(ContentType.JSON)
			.when()
				.get(USER_INFO_URI);
		}
	}
}
