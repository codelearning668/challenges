package sk.mkrajcovic.challenges.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ParticipantTest {

	private static final String JOHN = "John";

	@Test
	void equalsShouldReturnTrueWhenParticipantsHaveSameEqualityFields() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void equalsShouldReturnFalseWhenNameIsDifferent() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant("Jane", Duration.ofSeconds(10));

		assertNotEquals(first, second);
	}

	@Test
	void equalsShouldReturnFalseWhenBestLapTimeIsDifferent() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(11));

		assertNotEquals(first, second);
	}

	@Test
	void equalsShouldIgnoreChallenge() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		first.setChallenge(new Challenge());
		second.setChallenge(new Challenge());

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void equalsShouldBeReflexive() {
		var participant = participant(JOHN, Duration.ofSeconds(10));
		assertEquals(participant, participant);
	}

	@Test
	void equalsShouldReturnFalseWhenComparedWithNull() {
		var participant = participant(JOHN, Duration.ofSeconds(10));
		assertNotEquals(participant, null);
	}

	@Test
	void equalsShouldReturnFalseWhenComparedWithDifferentType() {
		var participant = participant(JOHN, Duration.ofSeconds(10));
		assertNotEquals(participant, JOHN);
	}

	@Test
	void hashCodeShouldBeConsistentForEqualParticipants() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

	@Test
	void setShouldTreatEqualParticipantsAsTheSameElement() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		Set<Participant> participants = new HashSet<>();

		assertTrue(participants.add(first));
		assertFalse(participants.add(second));

		assertEquals(1, participants.size());
		assertTrue(participants.contains(first));
		assertTrue(participants.contains(second));
	}

	@Test
	void setShouldContainParticipantsWithDifferentEqualityFields() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(11));

		Set<Participant> participants = new HashSet<>();

		assertTrue(participants.add(first));
		assertTrue(participants.add(second));

		assertEquals(2, participants.size());
	}

	@Test
	void setShouldNotDistinguishParticipantsByChallenge() {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		first.setChallenge(new Challenge());
		second.setChallenge(new Challenge());

		Set<Participant> participants = new HashSet<>();

		assertTrue(participants.add(first));
		assertFalse(participants.add(second));

		assertEquals(1, participants.size());
	}

	@Test
	void equalsShouldNotConsiderNameSearch() throws Exception {
		Participant first = participant(JOHN, Duration.ofSeconds(10));
		Participant second = participant(JOHN, Duration.ofSeconds(10));

		setNameSearch(first, "john");
		setNameSearch(second, "different");

		assertEquals(first, second);
	}

	private static Participant participant(String name, Duration bestLapTime) {
		var participant = new Participant();
		participant.setName(name);
		participant.setBestLapTime(bestLapTime);
		return participant;
	}

	private static void setNameSearch(Participant participant, String value) throws Exception {
		var field = Participant.class.getDeclaredField("nameSearch");
		field.setAccessible(true);
		field.set(participant, value);
	}
}
