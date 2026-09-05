package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.BAD_REQUEST;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CONFLICT;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CREATED;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.FORBIDDEN;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NOT_FOUND;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.OK;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNAUTHORIZED;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNPROCESSABLE_ENTITY;

import java.time.LocalDate;
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
import sk.mkrajcovic.challenges.controller.dto.CreateChallengeRequest;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.model.WheelDrive;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.security.UserRoles;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChallengeControllerTest {

    private static final String CHALLENGES_URI = "/challenges";
    private static final String REGISTER_URI = CHALLENGES_URI + "/{challengeId}/register";
    private static final String PARTICIPANT_URI = CHALLENGES_URI + "/{challengeId}/participant";

    private static final String ADMIN_USER = "challengeTestAdmin";
    private static final String ADMIN_PASS = "admin";
    private static final String PARTICIPANT_USER = "challengeTestParticipant";
    private static final String PARTICIPANT_PASS = "participant";
    private static final String SECOND_PARTICIPANT_USER = "challengeTestSecondPrticipant";

    private static final String TRACK_NAME = "Slovakia Ring";
    private static final String TRACK_COUNTRY = "Slovakia";
    private static final double TRACK_LENGTH_KM = 5.922;

    private static final String CAR_BRAND = "BMW";
    private static final String CAR_NAME = "M3";
    private static final int CAR_HP = 510;
    private static final int CAR_TORQUE = 650;

    private static final LocalDate VALID_END_DATE = LocalDate.now().plusDays(30);
    private static final LocalDate CLOSED_END_DATE = LocalDate.now().minusDays(1);
    private static final String VALID_LAP_TIME = "1:23.123";

    @LocalServerPort
    int port;

    @Autowired
    private InitHelper helper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChallengeRepository challengeRepository;

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

        var secondParticipant = new User();
        secondParticipant.setUsername(SECOND_PARTICIPANT_USER);
        secondParticipant.setPassword(passwordEncoder.encode(PARTICIPANT_PASS));
        secondParticipant.addAuthority(UserRoles.PARTICIPANT);
        secondParticipant.setEnabled(true);

        userRepository.saveAll(List.of(admin, participant, secondParticipant));
    }

    @Nested
    class CreateChallengeTest {

        @Test
        void adminCanCreateChallenge() {
            int trackId = createTrackAndReturnId();
            int carId = createCarAndReturnId();

            createChallenge(trackId, carId, VALID_END_DATE)
                .then()
                .statusCode(CREATED)
                .header("Location", allOf(
                    notNullValue(),
                    containsString(CHALLENGES_URI + "/")
                ));
        }

        @Test
        void unauthenticatedCannotCreateChallenge() {
            int trackId = createTrackAndReturnId();
            int carId = createCarAndReturnId();

            given()
                .contentType(ContentType.JSON)
                .body(new CreateChallengeRequest(trackId, carId, VALID_END_DATE))
            .when()
                .post(CHALLENGES_URI)
            .then()
                .statusCode(UNAUTHORIZED);
        }

        @Test
        void participantCannotCreateChallenge() {
            int trackId = createTrackAndReturnId();
            int carId = createCarAndReturnId();

            createChallengeAs(PARTICIPANT_USER, PARTICIPANT_PASS, trackId, carId, VALID_END_DATE)
                .then()
                .statusCode(FORBIDDEN);
        }

        @Nested
        class Validation {

            @Test
            void rejectsMissingTrackId() {
                createChallenge(null, 1, VALID_END_DATE).then().statusCode(BAD_REQUEST);
            }

            @Test
            void rejectsNonPositiveTrackId() {
                createChallenge(0, 1, VALID_END_DATE).then().statusCode(BAD_REQUEST);
                createChallenge(-1, 1, VALID_END_DATE).then().statusCode(BAD_REQUEST);
            }

            @Test
            void rejectsMissingCarId() {
                createChallenge(1, null, VALID_END_DATE).then().statusCode(BAD_REQUEST);
            }

            @Test
            void rejectsNonPositiveCarId() {
                createChallenge(1, 0, VALID_END_DATE).then().statusCode(BAD_REQUEST);
                createChallenge(1, -1, VALID_END_DATE).then().statusCode(BAD_REQUEST);
            }

            @Test
            void rejectsMissingEndDate() {
                createChallenge(1, 1, null).then().statusCode(BAD_REQUEST);
            }

            @Test
            void rejectsPastEndDate() {
                createChallenge(1, 1, CLOSED_END_DATE).then().statusCode(BAD_REQUEST);
            }
        }

        @Test
        void rejectsNonExistentTrack() {
            int carId = createCarAndReturnId();

            createChallenge(99999, carId, VALID_END_DATE)
                .then()
                .statusCode(NOT_FOUND);
        }

        @Test
        void rejectsNonExistentCar() {
            int trackId = createTrackAndReturnId();

            createChallenge(trackId, 99999, VALID_END_DATE)
                .then()
                .statusCode(NOT_FOUND);
        }

        @Test
        void rejectsDuplicateActiveChallengeForSameTrackAndCar() {
            int trackId = createTrackAndReturnId();
            int carId = createCarAndReturnId();

            // Bug fix test - simulates the case when duplicate challenge
            // could be created on the final day of the challenge !! 
            createChallenge(trackId, carId, LocalDate.now())
                .then()
                .statusCode(CREATED);

            createChallenge(trackId, carId, VALID_END_DATE)
                .then()
                .statusCode(CONFLICT);
        }
    }

    @Nested
    class GetChallengeTest {

        @Test
        void returnsChallengeDetails() {
            int challengeId = createChallengeAndReturnId();

            Response response = getChallenge(challengeId);

            response
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("challengeId", equalTo(challengeId))
                .body("challengeEndDate", equalTo(VALID_END_DATE.toString()))
                .body("trackName", equalTo(TRACK_NAME))
                .body("trackCountry", equalTo(TRACK_COUNTRY))
                .body("carBrand", equalTo(CAR_BRAND))
                .body("carName", equalTo(CAR_NAME))
                .body("carHorsePower", equalTo(CAR_HP))
                .body("carTorque", equalTo(CAR_TORQUE))
                .body("participants", hasSize(0));

            Number actualLengthKm = response.path("trackLengthKm");
            assertEquals(TRACK_LENGTH_KM, actualLengthKm.doubleValue(), 0.000001);
        }

        @Test
        void returnsRegisteredParticipant() {
            int challengeId = createChallengeAndRegisterParticipant();

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("participants.find { it.participantName == '" + PARTICIPANT_USER
                    + "' }.participantName", equalTo(PARTICIPANT_USER));
        }

        @Test
        void rejectsNonExistentChallenge() {
            getChallenge(99999).then().statusCode(NOT_FOUND);
        }

        @Test
        void rejectsZeroChallengeId() {
            getChallenge(0).then().statusCode(BAD_REQUEST);
        }

        @Test
        void rejectsNegativeChallengeId() {
            getChallenge(-1).then().statusCode(BAD_REQUEST);
        }
    }

    @Nested
    class SearchChallengesTest {

        @Test
        void listsChallenges() {
            int challengeId = createChallengeAndReturnId();

            searchChallenges()
                .then()
                .statusCode(OK)
                .contentType(ContentType.JSON)
                .body("find { it.challengeId == " + challengeId + " }", notNullValue());
        }

        @Test
        void searchesByEndDate() {
            int challengeId = createChallengeAndReturnId();

            searchChallenges("endDate", VALID_END_DATE.toString())
                .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void searchesByTrackNameCaseInsensitiveAndUsingContains() {
            int challengeId = createChallengeAndReturnId();

            searchChallenges("trackName", "OVAKIA RING")
                .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void searchesByTrackCountryIgnoringCaseAndDiacritics() {
            int challengeId = createChallengeAndReturnId(
                "Autodrom Česká republika",
                "Česká republika",
                TRACK_LENGTH_KM,
                CAR_BRAND,
                CAR_NAME
            );

            searchChallenges("trackCountry", "CESKA")
                .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void searchesByCarBrandCaseInsensitiveAndUsingContains() {
            int challengeId = createChallengeAndReturnId(
                TRACK_NAME,
                TRACK_COUNTRY,
                TRACK_LENGTH_KM,
                "BMW Motorsport",
                CAR_NAME
            );

            searchChallenges("carBrand", "bmw motor")
                .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void searchesByCarNameCaseInsensitiveAndUsingContains() {
            int challengeId = createChallengeAndReturnId(
                TRACK_NAME,
                TRACK_COUNTRY,
                TRACK_LENGTH_KM,
                CAR_BRAND,
                "M3 Competition"
            );

            searchChallenges("carName", "competition")
                .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void searchesUsingMultipleCriteria() {
            int challengeId = createChallengeAndReturnId();

            given()
                .params(
                    "endDate", VALID_END_DATE.toString(),
                    "trackName", TRACK_NAME,
                    "trackCountry", TRACK_COUNTRY,
                    "carBrand", CAR_BRAND,
                    "carName", CAR_NAME
                )
            .when()
                .get(CHALLENGES_URI)
            .then()
                .statusCode(OK)
                .body("challengeId", hasItem(challengeId));
        }

        @Test
        void returnsEmptyListWhenNothingMatches() {
            createChallengeAndReturnId();

            searchChallenges("trackName", "does-not-exist")
                .then()
                .statusCode(OK)
                .body("size()", is(0));
        }
    }

    @Nested
    class RegisterForChallengeTest {

        @Test
        void participantCanRegisterForChallenge() {
            int challengeId = createChallengeAndReturnId();

            registerForChallenge(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(OK);

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("participants.find { it.participantName == '" + PARTICIPANT_USER
                    + "' }.participantName", equalTo(PARTICIPANT_USER));
        }

        @Test
        void unauthenticatedUserCannotRegister() {
            int challengeId = createChallengeAndReturnId();

            given()
            .when()
                .post(REGISTER_URI, challengeId)
            .then()
                .statusCode(UNAUTHORIZED);
        }

        @Test
        void adminCannotRegister() {
            int challengeId = createChallengeAndReturnId();

            registerForChallenge(challengeId, ADMIN_USER, ADMIN_PASS)
                .then()
                .statusCode(FORBIDDEN);
        }

        @Test
        void rejectsNonExistentChallenge() {
            registerForChallenge(99999, PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(NOT_FOUND);
        }

        @Test
        void rejectsDuplicateRegistration() {
            int challengeId = createChallengeAndRegisterParticipant();

            registerForChallenge(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(CONFLICT);
        }

        @Test
        void rejectsRegistrationForClosedChallenge() {
            int challengeId = createChallengeAndReturnId();
            closeChallenge(challengeId);

            registerForChallenge(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(UNPROCESSABLE_ENTITY);
        }
    }

    @Nested
    class UpdateLapTimeTest {

        @Test
        void participantCanUpdateOwnLapTime() {
            int challengeId = createChallengeAndRegisterParticipant();

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(OK);

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("bestParticipantName", equalTo(PARTICIPANT_USER))
                .body("bestLapTime", equalTo("0" + VALID_LAP_TIME))
                .body("participants.find { it.participantName == '" + PARTICIPANT_USER
                    + "' }.participantBestLapTime", equalTo("0" + VALID_LAP_TIME));
        }

        @Test
        void adminCanRemoveCurrentLeaderLapTimeAndNextBestParticipantBecomesLeader() {
            int challengeId = createChallengeAndRegisterParticipant();

            registerForChallenge(challengeId, SECOND_PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(OK);

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, "1:23.123")
                .then()
                .statusCode(OK);
            updateLapTime(challengeId, SECOND_PARTICIPANT_USER, PARTICIPANT_PASS,
                    SECOND_PARTICIPANT_USER, "1:24.123")
                .then()
                .statusCode(OK);

            updateLapTime(challengeId, ADMIN_USER, ADMIN_PASS,
                    PARTICIPANT_USER, null)
                .then()
                .statusCode(OK);

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("bestParticipantName", equalTo(SECOND_PARTICIPANT_USER))
                .body("bestLapTime", equalTo("01:24.123"));
        }

        @Test
        void participantWithCurrentBestLapTimeBecomesSlowerAndNextBestParticipantBecomesLeader() {
            int challengeId = createChallengeAndRegisterParticipant();

            registerForChallenge(challengeId, SECOND_PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(OK);

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, "1:23.123")
                .then()
                .statusCode(OK);
            updateLapTime(challengeId, SECOND_PARTICIPANT_USER, PARTICIPANT_PASS,
                    SECOND_PARTICIPANT_USER, "1:24.123")
                .then()
                .statusCode(OK);

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, "1:25.123")
                .then()
                .statusCode(OK);

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("bestParticipantName", equalTo(SECOND_PARTICIPANT_USER))
                .body("bestLapTime", equalTo("01:24.123"));
        }

        @Test
        void participantCanDiscardOwnLapTime() {
            int challengeId = createChallengeAndRegisterParticipant();

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(OK);
            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, null)
                .then()
                .statusCode(OK);

            getChallenge(challengeId)
                .then()
                .statusCode(OK)
                .body("participants.find { it.participantName == '" + PARTICIPANT_USER
                    + "' }.participantBestLapTime", is(nullValue()));
        }

        @Test
        void adminCanUpdateParticipantLapTime() {
            int challengeId = createChallengeAndRegisterParticipant();

            updateLapTime(challengeId, ADMIN_USER, ADMIN_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(OK);
        }

        @Test
        void unauthenticatedUserCannotUpdateLapTime() {
            int challengeId = createChallengeAndRegisterParticipant();

            given()
                .contentType(ContentType.JSON)
                .body(lapTimeBody(PARTICIPANT_USER, VALID_LAP_TIME))
            .when()
                .put(PARTICIPANT_URI, challengeId)
            .then()
                .statusCode(UNAUTHORIZED);
        }

        @Test
        void rejectsMissingParticipantName() {
            int challengeId = createChallengeAndRegisterParticipant();

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    null, VALID_LAP_TIME)
                .then()
                .statusCode(BAD_REQUEST);
        }

        @Test
        void rejectsBlankParticipantName() {
            int challengeId = createChallengeAndRegisterParticipant();

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    " ", VALID_LAP_TIME)
                .then()
                .statusCode(BAD_REQUEST);
        }

        @Test
        void rejectsUnregisteredParticipant() {
            int challengeId = createChallengeAndReturnId();

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    "unregistered", VALID_LAP_TIME)
                .then()
                .statusCode(NOT_FOUND);
        }

        @Test
        void participantCannotUpdateAnotherRegisteredParticipantsLapTime() {
            int challengeId = createChallengeAndReturnId();

            registerForChallenge(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(OK);
            registerForChallenge(challengeId, SECOND_PARTICIPANT_USER, PARTICIPANT_PASS)
                .then()
                .statusCode(OK);

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    SECOND_PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(FORBIDDEN);
        }

        @Test
        void participantCannotUpdateLapTimeAfterChallengeEnded() {
            int challengeId = createChallengeAndRegisterParticipant();
            closeChallenge(challengeId);

            updateLapTime(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(UNPROCESSABLE_ENTITY);
        }

        @Test
        void adminCanUpdateLapTimeAfterChallengeEnded() {
            int challengeId = createChallengeAndRegisterParticipant();
            closeChallenge(challengeId);

            updateLapTime(challengeId, ADMIN_USER, ADMIN_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(OK);
        }

        @Test
        void rejectsNonExistentChallenge() {
            updateLapTime(99999, PARTICIPANT_USER, PARTICIPANT_PASS,
                    PARTICIPANT_USER, VALID_LAP_TIME)
                .then()
                .statusCode(NOT_FOUND);
        }
    }

    private Response createChallenge(Integer trackId, Integer carId, LocalDate endDate) {
        return createChallengeAs(ADMIN_USER, ADMIN_PASS, trackId, carId, endDate);
    }

    private Response createChallengeAs(String username, String password,
            Integer trackId, Integer carId, LocalDate endDate) {
        return given()
            .auth().preemptive().basic(username, password)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(new CreateChallengeRequest(trackId, carId, endDate))
        .when()
            .post(CHALLENGES_URI);
    }

    private int createChallengeAndReturnId() {
        return createChallengeAndReturnId(
            TRACK_NAME, TRACK_COUNTRY, TRACK_LENGTH_KM, CAR_BRAND, CAR_NAME
        );
    }

    private int createChallengeAndReturnId(String trackName, String trackCountry,
            double trackLengthKm, String carBrand, String carName) {
        int trackId = createTrackAndReturnId(trackName, trackCountry, trackLengthKm);
        int carId = createCarAndReturnId(carBrand, carName);

        String location = createChallenge(trackId, carId, VALID_END_DATE)
            .then()
            .statusCode(CREATED)
            .extract()
            .header("Location");

        assertNotNull(location);
        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    private int createChallengeAndRegisterParticipant() {
        int challengeId = createChallengeAndReturnId();

        registerForChallenge(challengeId, PARTICIPANT_USER, PARTICIPANT_PASS)
            .then()
            .statusCode(OK);

        return challengeId;
    }

    private Response getChallenge(int challengeId) {
        return given()
        .when()
            .get(CHALLENGES_URI + "/{challengeId}", challengeId);
    }

    private Response searchChallenges() {
        return given()
        .when()
            .get(CHALLENGES_URI);
    }

    private Response searchChallenges(String parameter, String value) {
        return given()
            .param(parameter, value)
        .when()
            .get(CHALLENGES_URI);
    }

    private Response registerForChallenge(int challengeId, String username, String password) {
        return given()
            .auth().preemptive().basic(username, password)
        .when()
            .post(REGISTER_URI, challengeId);
    }

    private Response updateLapTime(int challengeId, String username, String password,
            String participantName, String lapTime) {
        return given()
            .auth().preemptive().basic(username, password)
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(lapTimeBody(participantName, lapTime))
        .when()
            .put(PARTICIPANT_URI, challengeId);
    }

    private String lapTimeBody(String participantName, String lapTime) {
        String participantNameJson = participantName == null
            ? "null"
            : "\"" + participantName + "\"";
        String lapTimeJson = lapTime == null
            ? "null"
            : "\"" + lapTime + "\"";

        return """
            {
                "participantName": %s,
                "newLapTime": %s
            }
            """.formatted(participantNameJson, lapTimeJson);
    }

    private int createTrackAndReturnId() {
        return createTrackAndReturnId(TRACK_NAME, TRACK_COUNTRY, TRACK_LENGTH_KM);
    }

    private int createTrackAndReturnId(String name, String country, double lengthKm) {
        String location = given()
            .auth().preemptive().basic(ADMIN_USER, ADMIN_PASS)
            .contentType(ContentType.JSON)
            .body(new CreateTrackRequest(name, country, lengthKm))
        .when()
            .post("/tracks")
        .then()
            .statusCode(CREATED)
            .extract()
            .header("Location");

        assertNotNull(location);
        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    private int createCarAndReturnId() {
        return createCarAndReturnId(CAR_BRAND, CAR_NAME);
    }

    private int createCarAndReturnId(String brand, String name) {
        String location = given()
            .auth().preemptive().basic(ADMIN_USER, ADMIN_PASS)
            .contentType(ContentType.JSON)
            .body(new CreateCarRequest(brand, name, CAR_HP, CAR_TORQUE, WheelDrive.REAR))
        .when()
            .post("/cars")
        .then()
            .statusCode(CREATED)
            .extract()
            .header("Location");

        assertNotNull(location);
        return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
    }

    private void closeChallenge(int challengeId) {
        var challenge = challengeRepository.findById(challengeId).orElseThrow();
        challenge.setEndDate(CLOSED_END_DATE);
        challengeRepository.saveAndFlush(challenge);
    }
}
