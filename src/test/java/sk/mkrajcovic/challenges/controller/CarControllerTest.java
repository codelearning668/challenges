package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.BAD_REQUEST;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CREATED;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.FORBIDDEN;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NOT_FOUND;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.OK;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNAUTHORIZED;

import java.util.List;

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
import io.restassured.response.Response;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.model.WheelDrive;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.security.UserRoles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {

	private static final String ADMIN_USER = "admin";
	private static final String ADMIN_PASS = "admin";
	private static final String PARTICIPANT_USER = "participant";
	private static final String PARTICIPANT_PASS = "participant";

	private static final String VALID_BRAND = "BMW";
	private static final String VALID_NAME = "M3";
	private static final Integer VALID_HP = 510;
	private static final Integer VALID_TORQUE = 650;
	private static final WheelDrive VALID_DRIVE = WheelDrive.REAR;

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeAll
	void createUsers() {

		var admin = new User();
		admin.setUsername(ADMIN_USER);
		admin.setPassword(passwordEncoder.encode(ADMIN_PASS));
		admin.addAuthority(UserRoles.ADMIN);
		admin.setEnabled(true);

		var participant = new User();
		participant.setUsername(PARTICIPANT_USER);
		participant.setPassword(passwordEncoder.encode(PARTICIPANT_PASS));
		participant.addAuthority(UserRoles.PARTICIPANT);
		participant.setEnabled(true);

		userRepository.saveAll(List.of(admin, participant));
	}

	@Nested
	class CreateCarTest {

		@Nested
		class Positive {

			@Test
			void adminCanCreateCar() {
				createValidCar()
					.then()
						.statusCode(CREATED)
						.header("Location", allOf(notNullValue(), containsString("/cars/")));
			}
		}

		@Nested
		class Negative {

			@Nested
			class Security {

				@Test
				void unauthenticatedCannotCreateCar() {
					given()
						.contentType(ContentType.JSON)
						.accept(ContentType.JSON)
						.body(new CreateCarRequest(VALID_BRAND, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE))
					.when()
						.post("/cars")
					.then()
						.statusCode(UNAUTHORIZED);
				}

				@Test
				void unauthorizedCannotCreateCar() {
					given()
						.auth().preemptive().basic(PARTICIPANT_USER, PARTICIPANT_PASS)
						.contentType(ContentType.JSON)
						.accept(ContentType.JSON)
						.body(new CreateCarRequest(VALID_BRAND, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE))
					.when()
						.post("/cars")
					.then()
						.statusCode(FORBIDDEN);
				}
			}

			@Nested
			class Validation {

				@Test
				void withoutBrand() {
					createCar(null, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyBrand() {
					createCar(" ", VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withBrandTooLong() {
					createCar("x".repeat(51), VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withoutName() {
					createCar(VALID_BRAND, null, VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyName() {
					createCar(VALID_BRAND, " ", VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNameTooLong() {
					createCar(VALID_BRAND, "x".repeat(101), VALID_HP, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNegativeHp() {
					createCar(VALID_BRAND, VALID_NAME, -1, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withZeroHp() {
					createCar(VALID_BRAND, VALID_NAME, 0, VALID_TORQUE, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNegativeTorque() {
					createCar(VALID_BRAND, VALID_NAME, VALID_HP, -1, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withZeroTorque() {
					createCar(VALID_BRAND, VALID_NAME, VALID_HP, 0, VALID_DRIVE)
						.then()
							.statusCode(BAD_REQUEST);
				}
			}
		}
	}

	@Nested
	class GetCarTest {

		@Nested
		class Positive {

			@Test
			void canGetExistingCar() {
				int id = createCarAndReturnId();

				getCar(id)
					.then()
						.statusCode(OK)
						.contentType(ContentType.JSON)
						.body("id", equalTo(id))
						.body("brand", equalTo(VALID_BRAND))
						.body("name", equalTo(VALID_NAME))
						.body("horsePower", equalTo(VALID_HP))
						.body("torque", equalTo(VALID_TORQUE));
			}
		}

		@Nested
		class Negative {

			@Test
			void cannotGetNonExistentCar() {
				getCar(99999)
					.then()
						.statusCode(NOT_FOUND);
			}
		}
	}

	@Nested
	class SearchCarsTest {

		@Nested
		class Positive {

			@Test
			void canListAllCars() {
				searchCars()
					.then()
						.statusCode(OK)
						.contentType(ContentType.JSON);
			}

			@Test
			void canSearchByBrand() {
				createCar(VALID_BRAND, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE);

				searchCars(VALID_BRAND, null, null, null, null)
					.then()
						.statusCode(OK)
						.contentType(ContentType.JSON);
			}

			@Test
			void canSearchByWheelDrive() {
				createCar(VALID_BRAND, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE);

				searchCars(null, null, null, null, VALID_DRIVE)
					.then()
						.statusCode(OK)
						.contentType(ContentType.JSON);
			}

			@Test
			void returnsEmptyListWhenNoMatch() {
				searchCars("NonExistentBrand", null, null, null, null)
					.then()
						.statusCode(OK)
						.body("size()", is(0));
			}
		}
	}

	private Response createCar(String brand, String name, Integer hp, Integer torque, WheelDrive drive) {
		return given()
				.auth().preemptive().basic(ADMIN_USER, ADMIN_PASS)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(new CreateCarRequest(brand, name, hp, torque, drive))
			.when()
				.post("/cars");
	}

	private Response createValidCar() {
		return createCar(VALID_BRAND, VALID_NAME, VALID_HP, VALID_TORQUE, VALID_DRIVE);
	}

	private int createCarAndReturnId() {
		return Integer.parseInt(
			createValidCar()
				.then()
					.statusCode(CREATED)
					.extract()
					.header("Location")
				.replaceAll(".*(\\d+)$", "$1")
		);
	}

	private Response getCar(int id) {
		return given()
			.when()
				.get("/cars/{id}", id);
	}

	private Response searchCars(String brand, String name, Integer hp, Integer torque, WheelDrive drive) {
		var spec = given();
		if (brand != null) {
			spec = spec.param("brand", brand);
		}
		if (name != null) {
			spec = spec.param("name", name);
		}
		if (hp != null) {
			spec = spec.param("hp", hp);
		}
		if (torque != null) {
			spec = spec.param("torque", torque);
		}
		if (drive != null) {
			spec = spec.param("drive", drive.name());
		}
		return spec.when().get("/cars");
	}

	private Response searchCars() {
		return given()
			.when()
				.get("/cars");
	}
}
