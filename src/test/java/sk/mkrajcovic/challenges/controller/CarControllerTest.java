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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

	private static final String CAR_URI = "/cars";
	private static final String CAR_URI_WITH_ID = CAR_URI + "/{id}";

	private static final String ADMIN_USER = "admin";
	private static final String ADMIN_PASS = "admin";
	private static final String PARTICIPANT_USER = "participant";
	private static final String PARTICIPANT_PASS = "participant";

	private static final String VALID_BRAND = "BMW";
	private static final String VALID_NAME = "M3";
	private static final int VALID_HP = 510;
	private static final int VALID_TORQUE = 650;
	private static final WheelDrive VALID_DRIVE = WheelDrive.REAR;

	private static final String SEARCH_BRAND = "BMW Motorsport";
	private static final String SEARCH_NAME = "M3 Competition";
	private static final String DIACRITIC_BRAND = "Škoda";
	private static final String DIACRITIC_NAME = "Škoda Competition Edition";

	private static final int NON_EXISTENT_CAR_ID = 99999;

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void setup() {
		helper.init(port);
	}

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
						.header(
							"Location",
							allOf(
								notNullValue(),
								containsString(CAR_URI + "/")
							)
						);
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
						.body(validCarRequest())
					.when()
						.post(CAR_URI)
					.then()
						.statusCode(UNAUTHORIZED);
				}

				@Test
				void unauthorizedCannotCreateCar() {
					given()
						.auth()
							.preemptive()
							.basic(PARTICIPANT_USER, PARTICIPANT_PASS)
						.contentType(ContentType.JSON)
						.accept(ContentType.JSON)
						.body(validCarRequest())
					.when()
						.post(CAR_URI)
					.then()
						.statusCode(FORBIDDEN);
				}
			}

			@Nested
			class Validation {

				@Test
				void withoutBrand() {
					createCar(
						null,
						VALID_NAME,
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyBrand() {
					createCar(
						" ",
						VALID_NAME,
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withBrandTooLong() {
					createCar(
						"x".repeat(51),
						VALID_NAME,
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withoutName() {
					createCar(
						VALID_BRAND,
						null,
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyName() {
					createCar(
						VALID_BRAND,
						" ",
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNameTooLong() {
					createCar(
						VALID_BRAND,
						"x".repeat(101),
						VALID_HP,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNegativeHp() {
					createCar(
						VALID_BRAND,
						VALID_NAME,
						-1,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withZeroHp() {
					createCar(
						VALID_BRAND,
						VALID_NAME,
						0,
						VALID_TORQUE,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNegativeTorque() {
					createCar(
						VALID_BRAND,
						VALID_NAME,
						VALID_HP,
						-1,
						VALID_DRIVE
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withZeroTorque() {
					createCar(
						VALID_BRAND,
						VALID_NAME,
						VALID_HP,
						0,
						VALID_DRIVE
					)
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
				getCar(NON_EXISTENT_CAR_ID)
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

			@ParameterizedTest
			@ValueSource(strings = {
				"bmw",
				"BmW",
				"BMW MOTORSPORT"
			})
			void canSearchByBrandCaseInsensitive(String searchBrand) {
				int id = createCarAndReturnId(
					SEARCH_BRAND,
					VALID_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(searchBrand, null, null, null, null),
					id
				);
			}

			@ParameterizedTest
			@ValueSource(strings = {
				"BMW",
				"bm",
				"motorsport",
				"SPORT"
			})
			void canSearchByBrandUsingContains(String searchBrand) {
				int id = createCarAndReturnId(
					SEARCH_BRAND,
					VALID_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(searchBrand, null, null, null, null),
					id
				);
			}

			@Test
			void canSearchByBrandIgnoringDiacritics() {
				int id = createCarAndReturnId(
					DIACRITIC_BRAND,
					VALID_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars("skoda", null, null, null, null),
					id
				);
			}

			@Test
			void canSearchByBrandIgnoringCaseDiacriticsAndUsingContains() {
				String brand = "Škoda Motorsport";

				int id = createCarAndReturnId(
					brand,
					VALID_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars("KODA MOTOR", null, null, null, null),
					id
				);
			}

			@ParameterizedTest
			@ValueSource(strings = {
				"competition",
				"COMPETITION",
				"petit",
				"ition"
			})
			void canSearchByNameCaseInsensitiveAndUsingContains(String searchName) {
				int id = createCarAndReturnId(
					VALID_BRAND,
					SEARCH_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, searchName, null, null, null),
					id
				);
			}

			@Test
			void canSearchByNameIgnoringDiacritics() {
				int id = createCarAndReturnId(
					VALID_BRAND,
					DIACRITIC_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, "skoda", null, null, null),
					id
				);
			}

			@Test
			void canSearchByNameIgnoringCaseDiacriticsAndUsingContains() {
				int id = createCarAndReturnId(
					VALID_BRAND,
					DIACRITIC_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, "KODA COMP", null, null, null),
					id
				);
			}

			@Test
			void canSearchByHorsePower() {
				int id = createCarAndReturnId(
					SEARCH_BRAND,
					SEARCH_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, null, VALID_HP, null, null),
					id
				);
			}

			@Test
			void canSearchByTorque() {
				int id = createCarAndReturnId(
					SEARCH_BRAND,
					SEARCH_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, null, null, VALID_TORQUE, null),
					id
				);
			}

			@Test
			void canSearchByWheelDrive() {
				int id = createCarAndReturnId(
					VALID_BRAND,
					VALID_NAME,
					VALID_HP,
					VALID_TORQUE,
					VALID_DRIVE
				);

				assertSearchContainsCar(
					searchCars(null, null, null, null, VALID_DRIVE),
					id
				);
			}

			@Test
			void returnsEmptyListWhenNoMatch() {
				searchCars(
					"NonExistentBrand",
					null,
					null,
					null,
					null
				)
					.then()
						.statusCode(OK)
						.body("size()", is(0));
			}
		}
	}

	private Response createCar(
		String brand,
		String name,
		Integer hp,
		Integer torque,
		WheelDrive drive
	) {
		return given()
				.auth()
					.preemptive()
					.basic(ADMIN_USER, ADMIN_PASS)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(new CreateCarRequest(
					brand,
					name,
					hp,
					torque,
					drive
				))
			.when()
				.post(CAR_URI);
	}

	private Response createValidCar() {
		return createCar(
			VALID_BRAND,
			VALID_NAME,
			VALID_HP,
			VALID_TORQUE,
			VALID_DRIVE
		);
	}

	private CreateCarRequest validCarRequest() {
		return new CreateCarRequest(
			VALID_BRAND,
			VALID_NAME,
			VALID_HP,
			VALID_TORQUE,
			VALID_DRIVE
		);
	}

	private int createCarAndReturnId() {
		return createCarAndReturnId(
			VALID_BRAND,
			VALID_NAME,
			VALID_HP,
			VALID_TORQUE,
			VALID_DRIVE
		);
	}

	private int createCarAndReturnId(
		String brand,
		String name,
		Integer hp,
		Integer torque,
		WheelDrive drive
	) {
		String location = createCar(
			brand,
			name,
			hp,
			torque,
			drive
		)
			.then()
				.statusCode(CREATED)
				.extract()
				.header("Location");

		return Integer.parseInt(
			location.substring(location.lastIndexOf('/') + 1)
		);
	}

	private Response getCar(int id) {
		return given()
			.when()
				.get(CAR_URI_WITH_ID, id);
	}

	private Response searchCars(
		String brand,
		String name,
		Integer hp,
		Integer torque,
		WheelDrive drive
	) {
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

		return spec
			.when()
				.get(CAR_URI);
	}

	private Response searchCars() {
		return given()
			.when()
				.get(CAR_URI);
	}

	private void assertSearchContainsCar(Response response, int carId) {
		response.then()
			.body(
				"find { it.id == " + carId + " }",
				notNullValue()
			);
	}
}
