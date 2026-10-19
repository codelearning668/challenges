package sk.mkrajcovic.challenges.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.BAD_REQUEST;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CONFLICT;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.CREATED;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.FORBIDDEN;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NOT_FOUND;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.NO_CONTENT;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.OK;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNAUTHORIZED;
import static sk.mkrajcovic.challenges.test.util.HttpCodes.UNPROCESSABLE_ENTITY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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
class ConversationEntryApiTest {

	private static final String CHALLENGES_URI = "/challenges";
	private static final String CREATE_ENTRY_URI = CHALLENGES_URI + "/{challengeId}/conversation";
	private static final String REPLY_URI = CHALLENGES_URI + "/{challengeId}/conversation/{parentEntryId}";
	private static final String ENTRY_URI = "/conversation-entries/{entryId}";
	private static final String REGISTER_URI = CHALLENGES_URI + "/{challengeId}/registration";
	private static final String PARTICIPANT_URI = CHALLENGES_URI + "/{challengeId}/participant";
	private static final String ADMIN_USER = "conversationApiAdmin";
	private static final String ADMIN_PASSWORD = "admin";
	private static final String PARTICIPANT_PASSWORD = "participant";
	private static final int SIMULATOR_ID = 1;
	private static final LocalDate VALID_END_DATE = LocalDate.now().plusDays(30);

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
	void createAdmin() {
		var admin = new User();
		admin.setUsername(ADMIN_USER);
		admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
		admin.addAuthority(UserRoles.ADMIN);
		admin.setEnabled(true);
		userRepository.save(admin);
	}

	@Nested
	class CreateTopLevelEntryTest {

		@Test
		void registeredParticipantCreatesEntryWithAuthorAndLapTimeSnapshot() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			updateLapTime(challengeId, participant, "1:23.456").then().statusCode(NO_CONTENT);

			Response response = postEntry(challengeId, participant, "Good luck everyone!");
			String location = response.then()
				.statusCode(CREATED)
				.extract()
				.header("Location");
			assertNotNull(location);
			assertTrue(location.matches(".*/conversation-entries/\\d+$"));
			int entryId = extractId(location);

			updateLapTime(challengeId, participant, "1:22.000").then().statusCode(NO_CONTENT);

			getChallenge(challengeId).then()
				.statusCode(OK)
				.body("conversation", hasSize(1))
				.body("conversation[0].entryId", equalTo(entryId))
				.body("conversation[0].parentEntryId", nullValue())
				.body("conversation[0].message", equalTo("Good luck everyone!"))
				.body("conversation[0].participantName", equalTo(participant))
				.body("conversation[0].participantLapTimeAtPosting", equalTo("01:23.456"))
				.body("conversation[0].createdAt", is(notNullValue()));
		}

		@Test
		void registeredParticipantWithoutLapTimeCanCreateEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);

			postEntry(challengeId, participant, "No recorded time yet")
				.then().statusCode(CREATED);

			getChallenge(challengeId).then()
				.statusCode(OK)
				.body("conversation[0].participantLapTimeAtPosting", nullValue());
		}

		@Test
		void rejectsMessageLongerThanOneThousandCharacters() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);

			postEntry(challengeId, participant, "x".repeat(1001))
				.then().statusCode(BAD_REQUEST);
		}

		@Test
		void unauthenticatedUserCannotCreateEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);

			given().contentType(ContentType.JSON).body(messageBody("message"))
				.when().post(CREATE_ENTRY_URI, challengeId)
				.then().statusCode(UNAUTHORIZED);
		}

		@Test
		void administratorCannotCreateEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);

			postEntry(challengeId, ADMIN_USER, "Admin is not a participant")
				.then().statusCode(FORBIDDEN);
		}

		@Test
		void rejectsParticipantWhoIsNotRegisteredForChallenge() {
			String participant = createParticipant();
			int challengeId = createChallengeAndReturnId();

			postEntry(challengeId, participant, "Not registered")
				.then().statusCode(CONFLICT);
		}

		@Test
		void rejectsEntryForNonExistentChallenge() {
			String participant = createParticipant();

			postEntry(99999, participant, "Missing challenge")
				.then().statusCode(NOT_FOUND);
		}

		@Test
		void rejectsEntryForClosedChallenge() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			closeChallenge(challengeId);

			postEntry(challengeId, participant, "Challenge is closed")
				.then().statusCode(UNPROCESSABLE_ENTITY);
		}

		@Test
		void rejectsZeroAndNegativeChallengeIds() {
			String participant = createParticipant();

			postEntry(0, participant, "Invalid challenge ID").then().statusCode(BAD_REQUEST);
			postEntry(-1, participant, "Invalid challenge ID").then().statusCode(BAD_REQUEST);
		}
	}

	@Nested
	class ReplyTest {

		@Test
		void registeredParticipantCanReplyAndReplyLocationIsCanonicalResourceUri() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentEntryId = extractCreatedEntryId(postEntry(challengeId, participant, "Original message"));

			Response replyResponse = postReply(challengeId, parentEntryId, participant, "A reply");
			assertEquals(CREATED, replyResponse.statusCode(), replyResponse.asString());
			String location = replyResponse.getHeader("Location");
			assertNotNull(location);
			assertTrue(location.matches(".*/conversation-entries/\\d+$"));
			int replyId = extractId(location);

			getChallenge(challengeId).then()
				.statusCode(OK)
				.body("conversation", hasSize(2))
				.body("conversation[0].entryId", equalTo(parentEntryId))
				.body("conversation[1].entryId", equalTo(replyId))
				.body("conversation[1].parentEntryId", equalTo(parentEntryId))
				.body("conversation[1].message", equalTo("A reply"))
				.body("conversation[1].participantName", equalTo(participant));
		}

		@Test
		void unauthenticatedUserCannotReply() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentEntryId = extractCreatedEntryId(postEntry(challengeId, participant, "Parent"));

			given().contentType(ContentType.JSON).body(messageBody("Reply"))
				.when().post(REPLY_URI, challengeId, parentEntryId)
				.then().statusCode(UNAUTHORIZED);
		}

		@Test
		void administratorCannotReply() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentEntryId = extractCreatedEntryId(postEntry(challengeId, participant, "Parent"));

			postReply(challengeId, parentEntryId, ADMIN_USER, "Admin reply")
				.then().statusCode(FORBIDDEN);
		}

		@Test
		void rejectsUnregisteredParticipantReply() {
			String participant = createParticipant();
			String unregistered = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentEntryId = extractCreatedEntryId(postEntry(challengeId, participant, "Parent"));

			postReply(challengeId, parentEntryId, unregistered, "Not registered")
				.then().statusCode(CONFLICT);
		}

		@Test
		void rejectsReplyWhenChallengeIsClosed() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentEntryId = extractCreatedEntryId(postEntry(challengeId, participant, "Parent"));
			closeChallenge(challengeId);

			postReply(challengeId, parentEntryId, participant, "Too late")
				.then().statusCode(UNPROCESSABLE_ENTITY);
		}

		@Test
		void rejectsReplyToMissingParentEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);

			postReply(challengeId, 99999, participant, "Missing parent")
				.then().statusCode(UNPROCESSABLE_ENTITY);
		}

		@Test
		void rejectsReplyToEntryFromAnotherChallenge() {
			String firstParticipant = createParticipant();
			String secondParticipant = createParticipant();
			int firstChallengeId = createChallengeAndRegisterParticipant(firstParticipant);
			int otherChallengeId = createChallengeAndRegisterParticipant(secondParticipant);
			int parentEntryId = extractCreatedEntryId(postEntry(firstChallengeId, firstParticipant, "Other challenge parent"));

			postReply(otherChallengeId, parentEntryId, secondParticipant, "Cross-challenge reply")
				.then().statusCode(UNPROCESSABLE_ENTITY);
		}

		@Test
		void rejectsNonPositiveChallengeAndParentIds() {
			String participant = createParticipant();

			postReply(0, 1, participant, "Invalid IDs").then().statusCode(BAD_REQUEST);
			postReply(-1, 1, participant, "Invalid IDs").then().statusCode(BAD_REQUEST);
			postReply(1, 0, participant, "Invalid IDs").then().statusCode(BAD_REQUEST);
			postReply(1, -1, participant, "Invalid IDs").then().statusCode(BAD_REQUEST);
		}
	}

	@Nested
	class DeleteEntryTest {

		@Test
		void administratorDeletesEntryAndPreservesRepliesAsTopLevelEntries() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int parentId = extractCreatedEntryId(postEntry(challengeId, participant, "Parent"));
			int replyId = extractCreatedEntryId(postReply(challengeId, parentId, participant, "Direct reply"));
			int unrelatedId = extractCreatedEntryId(postEntry(challengeId, participant, "Unrelated entry"));

			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, parentId)
				.then().statusCode(NO_CONTENT).body(is(""));

			getChallenge(challengeId).then()
				.statusCode(OK)
				.body("conversation", hasSize(2))
				.body("conversation.find { it.entryId == " + replyId + " }.parentEntryId", nullValue())
				.body("conversation.find { it.entryId == " + replyId + " }.message", equalTo("Direct reply"))
				.body("conversation.find { it.entryId == " + unrelatedId + " }.parentEntryId", nullValue());
		}

		@Test
		void unauthenticatedUserCannotDeleteEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int entryId = extractCreatedEntryId(postEntry(challengeId, participant, "Entry"));

			given().when().delete(ENTRY_URI, entryId).then().statusCode(UNAUTHORIZED);
		}

		@Test
		void participantCannotDeleteEntry() {
			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int entryId = extractCreatedEntryId(postEntry(challengeId, participant, "Entry"));

			given().auth().preemptive().basic(participant, PARTICIPANT_PASSWORD)
				.when().delete(ENTRY_URI, entryId).then().statusCode(FORBIDDEN);
		}

		@Test
		void rejectsNonPositiveEntryIds() {
			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, 0).then().statusCode(BAD_REQUEST);
			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, -1).then().statusCode(BAD_REQUEST);
		}

		@Test
		void rejectsMissingOrAlreadyDeletedEntry() {
			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, 99999).then().statusCode(NOT_FOUND);

			String participant = createParticipant();
			int challengeId = createChallengeAndRegisterParticipant(participant);
			int entryId = extractCreatedEntryId(postEntry(challengeId, participant, "Entry"));
			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, entryId).then().statusCode(NO_CONTENT);
			given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
				.when().delete(ENTRY_URI, entryId).then().statusCode(NOT_FOUND);
		}
	}

	private String createParticipant() {
		String username = "conversation-" + UUID.randomUUID();
		var participant = new User();
		participant.setUsername(username);
		participant.setPassword(passwordEncoder.encode(PARTICIPANT_PASSWORD));
		participant.addAuthority(UserRoles.PARTICIPANT);
		participant.setEnabled(true);
		userRepository.save(participant);
		return username;
	}

	private int createChallengeAndRegisterParticipant(String participant) {
		int challengeId = createChallengeAndReturnId();
		registerParticipant(challengeId, participant).then().statusCode(NO_CONTENT);
		return challengeId;
	}

	private int createChallengeAndReturnId() {
		String suffix = UUID.randomUUID().toString();
		int trackId = extractId(given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
			.contentType(ContentType.JSON)
			.body(new CreateTrackRequest("Track " + suffix, "Testland", BigDecimal.valueOf(5.5), SIMULATOR_ID, false))
			.when().post("/tracks").then().statusCode(CREATED).extract().header("Location"));
		int carId = extractId(given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
			.contentType(ContentType.JSON)
			.body(new CreateCarRequest("Brand " + suffix, "Car " + suffix, 500, 600, WheelDrive.REAR, SIMULATOR_ID, false))
			.when().post("/cars").then().statusCode(CREATED).extract().header("Location"));
		return extractId(given().auth().preemptive().basic(ADMIN_USER, ADMIN_PASSWORD)
			.contentType(ContentType.JSON)
			.body(new CreateChallengeRequest(trackId, carId, VALID_END_DATE))
			.when().post(CHALLENGES_URI).then().statusCode(CREATED).extract().header("Location"));
	}

	private Response registerParticipant(int challengeId, String participant) {
		return given().auth().preemptive().basic(participant, PARTICIPANT_PASSWORD)
			.when().post(REGISTER_URI, challengeId);
	}

	private Response postEntry(int challengeId, String participant, String message) {
		return given().auth().preemptive().basic(participant, participant.equals(ADMIN_USER) ? ADMIN_PASSWORD : PARTICIPANT_PASSWORD)
			.contentType(ContentType.JSON).body(messageBody(message))
			.when().post(CREATE_ENTRY_URI, challengeId);
	}

	private Response postReply(int challengeId, int parentEntryId, String participant, String message) {
		return given().auth().preemptive().basic(participant, participant.equals(ADMIN_USER) ? ADMIN_PASSWORD : PARTICIPANT_PASSWORD)
			.contentType(ContentType.JSON).body(messageBody(message))
			.when().post(REPLY_URI, challengeId, parentEntryId);
	}

	private Response updateLapTime(int challengeId, String participant, String lapTime) {
		return given().auth().preemptive().basic(participant, PARTICIPANT_PASSWORD)
			.contentType(ContentType.JSON).body("{\"participantName\":\"" + participant + "\",\"newLapTime\":\"" + lapTime + "\"}")
			.when().patch(PARTICIPANT_URI, challengeId);
	}

	private Response getChallenge(int challengeId) {
		return given().when().get(CHALLENGES_URI + "/{challengeId}", challengeId);
	}

	private void closeChallenge(int challengeId) {
		var challenge = challengeRepository.findById(challengeId).orElseThrow();
		challenge.setEndDate(LocalDate.now().minusDays(1));
		challengeRepository.saveAndFlush(challenge);
	}

	private String messageBody(String message) {
		return "{\"message\":\"" + message + "\"}";
	}

	private int extractCreatedEntryId(Response response) {
		return extractId(response.then().statusCode(CREATED).extract().header("Location"));
	}

	private int extractId(String location) {
		assertNotNull(location);
		return Integer.parseInt(location.substring(location.lastIndexOf('/') + 1));
	}
}
