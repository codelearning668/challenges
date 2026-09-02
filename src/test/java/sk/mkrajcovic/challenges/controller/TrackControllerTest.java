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

import org.junit.jupiter.api.Assertions;
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
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.security.UserRoles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrackControllerTest {

	private static final String TRACK_URI = "/tracks";
	private static final String TRACK_URI_WITH_ID = TRACK_URI + "/{trackId}";

	private static final String ADMIN_USER = "admin";
	private static final String ADMIN_PASS = "admin";
	private static final String PARTICIPANT_USER = "participant";
	private static final String PARTICIPANT_PASS = "participant";

	private static final String VALID_COUNTRY = "Slovakia";
	private static final String VALID_NAME = "Slovakia Ring";
	private static final double VALID_LENGTH_KM = 5.922;

	private static final String SEARCH_COUNTRY = "Slovakia";
	private static final String SEARCH_NAME = "Slovakia Ring Grand Prix";
	private static final double SEARCH_LENGTH_KM = 5.922;

	private static final String DIACRITIC_COUNTRY = "Česká republika";
	private static final String DIACRITIC_NAME = "Autodrom Česká republika";

	private static final int NON_EXISTENT_TRACK_ID = 99999;

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
	class CreateTrackTest {

		@Nested
		class Positive {

			@Test
			void adminCanCreateTrack() {
				createValidTrack()
					.then()
						.statusCode(CREATED)
						.header(
							"Location",
							allOf(
								notNullValue(),
								containsString(TRACK_URI + "/")
							)
						);
			}
		}

		@Nested
		class Negative {

			@Nested
			class Security {

				@Test
				void unauthenticatedCannotCreateTrack() {
					given()
						.contentType(ContentType.JSON)
						.accept(ContentType.JSON)
						.body(validTrackRequest())
					.when()
						.post(TRACK_URI)
					.then()
						.statusCode(UNAUTHORIZED);
				}

				@Test
				void unauthorizedCannotCreateTrack() {
					given()
						.auth()
							.preemptive()
							.basic(PARTICIPANT_USER, PARTICIPANT_PASS)
						.contentType(ContentType.JSON)
						.accept(ContentType.JSON)
						.body(validTrackRequest())
					.when()
						.post(TRACK_URI)
					.then()
						.statusCode(FORBIDDEN);
				}
			}

			@Nested
			class Validation {

				@Test
				void withoutName() {
					createTrack(
						null,
						VALID_COUNTRY,
						VALID_LENGTH_KM
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withEmptyName() {
					createTrack(
						" ",
						VALID_COUNTRY,
						VALID_LENGTH_KM
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNameTooLong() {
					createTrack(
						"x".repeat(101),
						VALID_COUNTRY,
						VALID_LENGTH_KM
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withCountryTooLong() {
					createTrack(
						VALID_NAME,
						"x".repeat(101),
						VALID_LENGTH_KM
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withNegativeLength() {
					createTrack(
						VALID_NAME,
						VALID_COUNTRY,
						-1.0
					)
						.then()
							.statusCode(BAD_REQUEST);
				}

				@Test
				void withZeroLength() {
					createTrack(
						VALID_NAME,
						VALID_COUNTRY,
						0.0
					)
						.then()
							.statusCode(BAD_REQUEST);
				}
			}
		}
	}

	@Nested
	class GetTrackTest {

		@Nested
		class Positive {

			@Test
			void canGetExistingTrack() {
				int id = createTrackAndReturnId();

				Response response = getTrack(id);

				response.then()
					.statusCode(OK)
					.contentType(ContentType.JSON)
					.body("id", equalTo(id))
					.body("name", equalTo(VALID_NAME))
					.body("country", equalTo(VALID_COUNTRY));

				// because RestAssured parser will give me a Float
				Number actualLengthKm = response.path("lengthKm");

				Assertions.assertEquals(
					VALID_LENGTH_KM,
					actualLengthKm.doubleValue(),
					0.000001
				);
			}
		}

		@Nested
		class Negative {

			@Test
			void cannotGetNonExistentTrack() {
				getTrack(NON_EXISTENT_TRACK_ID)
					.then()
						.statusCode(NOT_FOUND);
			}
		}
	}

	@Nested
	class SearchTracksTest {

		@Nested
		class Positive {

			@Test
			void canListAllTracks() {
				searchTracks()
					.then()
						.statusCode(OK)
						.contentType(ContentType.JSON);
			}

			@ParameterizedTest
			@ValueSource(strings = {
				"slovakia",
				"SLOVAKIA",
				"SlOvAkIa"
			})
			void canSearchByCountryCaseInsensitive(String searchCountry) {
				int id = createTrackAndReturnId(
					SEARCH_NAME,
					SEARCH_COUNTRY,
					SEARCH_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks(searchCountry, null, null),
					id
				);
			}

			@Test
			void canSearchByCountryIgnoringDiacritics() {
				int id = createTrackAndReturnId(
					VALID_NAME,
					DIACRITIC_COUNTRY,
					VALID_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks("ceska", null, null),
					id
				);
			}

			@Test
			void canSearchByCountryIgnoringCaseDiacriticsAndUsingContains() {
				int id = createTrackAndReturnId(
					VALID_NAME,
					DIACRITIC_COUNTRY,
					VALID_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks("ESKA REP", null, null),
					id
				);
			}

			@ParameterizedTest
			@ValueSource(strings = {
				"ring",
				"RING",
				"grand",
				"prix"
			})
			void canSearchByNameCaseInsensitiveAndUsingContains(String searchName) {
				int id = createTrackAndReturnId(
					SEARCH_NAME,
					SEARCH_COUNTRY,
					SEARCH_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks(null, searchName, null),
					id
				);
			}

			@Test
			void canSearchByNameIgnoringDiacritics() {
				int id = createTrackAndReturnId(
					DIACRITIC_NAME,
					VALID_COUNTRY,
					VALID_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks(null, "ceska", null),
					id
				);
			}

			@Test
			void canSearchByNameIgnoringCaseDiacriticsAndUsingContains() {
				int id = createTrackAndReturnId(
					DIACRITIC_NAME,
					VALID_COUNTRY,
					VALID_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks(null, "AUTODROM CES", null),
					id
				);
			}

			@Test
			void canSearchByLength() {
				int id = createTrackAndReturnId(
					SEARCH_NAME,
					SEARCH_COUNTRY,
					SEARCH_LENGTH_KM
				);

				assertSearchContainsTrack(
					searchTracks(null, null, SEARCH_LENGTH_KM),
					id
				);
			}

			@Test
			void returnsEmptyListWhenNoMatch() {
				searchTracks(
					"NonExistentCountry",
					null,
					null
				)
					.then()
						.statusCode(OK)
						.body("size()", is(0));
			}
		}
	}

	private Response createTrack(
		String name,
		String country,
		Double lengthKm
	) {
		return given()
				.auth()
					.preemptive()
					.basic(ADMIN_USER, ADMIN_PASS)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(new CreateTrackRequest(
					name,
					country,
					lengthKm
				))
			.when()
				.post(TRACK_URI);
	}

	private Response createValidTrack() {
		return createTrack(
			VALID_NAME,
			VALID_COUNTRY,
			VALID_LENGTH_KM
		);
	}

	private CreateTrackRequest validTrackRequest() {
		return new CreateTrackRequest(
			VALID_NAME,
			VALID_COUNTRY,
			VALID_LENGTH_KM
		);
	}

	private int createTrackAndReturnId() {
		return createTrackAndReturnId(
			VALID_NAME,
			VALID_COUNTRY,
			VALID_LENGTH_KM
		);
	}

	private int createTrackAndReturnId(
		String name,
		String country,
		Double lengthKm
	) {
		String location = createTrack(
			name,
			country,
			lengthKm
		)
			.then()
				.statusCode(CREATED)
				.extract()
				.header("Location");

		return Integer.parseInt(
			location.substring(location.lastIndexOf('/') + 1)
		);
	}

	private Response getTrack(int trackId) {
		return given()
			.when()
				.get(TRACK_URI_WITH_ID, trackId);
	}

	private Response searchTracks(
		String country,
		String name,
		Double lengthKm
	) {
		var spec = given();

		if (country != null) {
			spec = spec.param("country", country);
		}
		if (name != null) {
			spec = spec.param("name", name);
		}
		if (lengthKm != null) {
			spec = spec.param("lengthKm", lengthKm);
		}

		return spec
			.when()
				.get(TRACK_URI);
	}

	private Response searchTracks() {
		return given()
			.when()
				.get(TRACK_URI);
	}

	private void assertSearchContainsTrack(
		Response response,
		int trackId
	) {
		response.then()
			.body(
				"find { it.id == " + trackId + " }",
				notNullValue()
			);
	}
}
