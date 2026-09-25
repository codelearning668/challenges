package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimulatorControllerTest {

	private static final String SIMULATORS_URI = "/simulators";

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Test
	void listsSupportedSimulatorsInIdentifierOrder() {
		given()
			.accept(ContentType.JSON)
		.when()
			.get(SIMULATORS_URI)
		.then()
			.statusCode(200)
			.contentType(ContentType.JSON)
			.body("id", contains(1, 2, 3))
			.body("name", contains(
				"Assetto Corsa",
				"Assetto Corsa Competizione",
				"WRC Generations"
			));
	}
}
