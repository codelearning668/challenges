package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.test.util.EntityTestUtils;

class ChallengeMapperTest {

	@Test
	void shouldMapChallengeToDetailResponse() {
		var track = createTrack();
		var car = createCar();

		var bestLapTime = Duration.ofMinutes(123).plusSeconds(45);

		var participant = new Participant();
		EntityTestUtils.setId(participant, 30);
		participant.setName("John");
		participant.setBestLapTime(bestLapTime);

		var challenge = new Challenge();
		EntityTestUtils.setId(challenge, 1);
		challenge.setEndDate(LocalDate.of(2026, 9, 30));
		challenge.setTrack(track);
		challenge.setCar(car);
		challenge.setParticipants(Set.of(participant));

		var response = ChallengeMapper.toDetailResponse(challenge);

		assertAll(() -> assertEquals(1, response.getChallengeId()),
			() -> assertEquals(LocalDate.of(2026, 9, 30), response.getChallengeEndDate()),
			() -> assertEquals(10, response.getTrackId()),
			() -> assertEquals("Slovakia", response.getTrackCountry()),
			() -> assertEquals("Slovakia Ring", response.getTrackName()),
			() -> assertEquals(5.922, response.getTrackLengthKm()),
			() -> assertEquals(20, response.getCarId()),
			() -> assertEquals("BMW", response.getCarBrand()),
			() -> assertEquals("M3", response.getCarName()),
			() -> assertEquals(510, response.getCarHorsePower()),
			() -> assertEquals(650, response.getCarTorque()),
			() -> assertEquals(1, response.getParticipants().size()),
			() -> assertEquals(30, response.getParticipants().getFirst().participantId()),
			() -> assertEquals("John", response.getParticipants().getFirst().participantName()),
			() -> assertEquals(bestLapTime, response.getParticipants().getFirst().participantBestLapTime()));
	}

	@Test
	void shouldMapChallengeWithoutParticipants() {
		var challenge = new Challenge();
		EntityTestUtils.setId(challenge, 1);
		challenge.setTrack(createTrack());
		challenge.setCar(createCar());
		challenge.setParticipants(Set.of());

		var response = ChallengeMapper.toDetailResponse(challenge);
		assertTrue(response.getParticipants().isEmpty());
	}

	@Test
	void shouldMapParticipantToDetailResponse() {
		var bestLapTime = Duration.ofMinutes(118).plusSeconds(25);

		var participant = new Participant();
		EntityTestUtils.setId(participant, 42);
		participant.setName("Michael");
		participant.setBestLapTime(bestLapTime);

		var response = ChallengeMapper.toParticipantDetailResponse(participant);

		assertAll(
			() -> assertEquals(42, response.participantId()),
			() -> assertEquals("Michael", response.participantName()),
			() -> assertEquals(bestLapTime, response.participantBestLapTime()));
	}

	@Test
	void shouldRejectNullChallenge() {
		var exception = assertThrows(NullPointerException.class, () -> ChallengeMapper.toDetailResponse(null));
		assertEquals("challenge cannot be null in order to map its values", exception.getMessage());
	}

	@Test
	void shouldRejectNullParticipant() {
		var exception = assertThrows(
			NullPointerException.class,
			() -> ChallengeMapper.toParticipantDetailResponse(null)
		);
		assertEquals("participant cannot be null in order to map its values", exception.getMessage());
	}

	private static Track createTrack() {
		var track = new Track();
		EntityTestUtils.setId(track, 10);
		track.setCountry("Slovakia");
		track.setName("Slovakia Ring");
		track.setLengthKm(5.922);
		return track;
	}

	private static Car createCar() {
		var car = new Car();
		EntityTestUtils.setId(car, 20);
		car.setBrand("BMW");
		car.setName("M3");
		car.setHorsePower(510);
		car.setTorque(650);
		return car;
	}
}
