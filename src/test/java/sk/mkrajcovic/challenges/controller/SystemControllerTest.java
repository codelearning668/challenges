package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

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
class SystemControllerTest {

	private static final String USER_INFO_URI = "/users/info";

	private static final String VALID_USERNAME = "ParticipantUser";
	private static final String VALID_PASSWORD = "ParticipantUserPwd";

	@LocalServerPort
	int port;

	@Autowired private InitHelper helper;
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private UserRepository userRepository;

	@BeforeAll
	void createUser() {
		var participant = new User();
		participant.setUsername(VALID_USERNAME);
		participant.setPassword(passwordEncoder.encode(VALID_PASSWORD));
		participant.addAuthority(UserRoles.PARTICIPANT);
		participant.setEnabled(true);

		userRepository.save(participant);
	}

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Nested
	class UserInfoTest {

		@Test
		void authenticatedUserCanGetOwnInfo() {
			given()
				.auth()
					.preemptive()
					.basic(VALID_USERNAME, VALID_PASSWORD)
				.accept(ContentType.JSON)
			.when()
				.get(USER_INFO_URI)
			.then()
				.statusCode(200)
				.body("username", equalTo(VALID_USERNAME))
				.body("roles", contains(UserRoles.PARTICIPANT));
		}
	}
}
