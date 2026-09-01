package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.ChallengeDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.ParticipantDetailResponse;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.Participant;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ChallengeMapper {

	public static ChallengeDetailResponse toDetailResponse(Challenge challenge) {
		Objects.requireNonNull(challenge, "challenge cannot be null in order to map its values");

		var challengeDetail = new ChallengeDetailResponse();
		challengeDetail.setChallengeId(challenge.getId());
		challengeDetail.setChallengeEndDate(challenge.getEndDate());

		// TODO: handle NPEs here or ensure there are none

		var track = challenge.getTrack();
		challengeDetail.setTrackId(track.getId());
		challengeDetail.setTrackCountry(track.getCountry());
		challengeDetail.setTrackName(track.getName());
		challengeDetail.setTrackLengthKm(track.getLengthKm());

		var car = challenge.getCar();
		challengeDetail.setCarId(car.getId());
		challengeDetail.setCarBrand(car.getBrand());
		challengeDetail.setCarName(car.getName());
		challengeDetail.setCarHorsePower(car.getHorsePower());
		challengeDetail.setCarTorque(car.getTorque());

		var participantDetails = challenge.getParticipants().stream()
			.map(ChallengeMapper::toParticipantDetailResponse)
			.toList();

		challengeDetail.getParticipants().addAll(participantDetails);

		return challengeDetail;
	}

	public static ParticipantDetailResponse toParticipantDetailResponse(Participant participant) {
		Objects.requireNonNull(participant, "participant cannot be null in order to map its values");

		return new ParticipantDetailResponse(
			participant.getId(),
			participant.getName(),
			participant.getBestLapTime()
		);
	}
}
