package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.ChallengeDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.ChallengeSummaryResponse;
import sk.mkrajcovic.challenges.controller.dto.ParticipantDetailResponse;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.ChallengeDetail;
import sk.mkrajcovic.challenges.util.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChallengeMapper {

	public static ChallengeDetailResponse toDetailResponse(Challenge challenge) {
		Objects.requireNonNull(challenge, "challenge cannot be null in order to map its values");

		var challengeDetail = new ChallengeDetailResponse();
		challengeDetail.setChallengeId(challenge.getId());
		challengeDetail.setChallengeEndDate(challenge.getEndDate());
		challengeDetail.setSimulatorName(challenge.getCar().getSimulator().getType().getDisplayName());
		challengeDetail.setBestParticipantName(challenge.getBestParticipantName());
		challengeDetail.setBestLapTime(challenge.getBestLapTime());

		var track = Value.firstNonNull(challenge.getTrack(), new Track());
		challengeDetail.setTrackId(track.getId());
		challengeDetail.setTrackCountry(track.getCountry());
		challengeDetail.setTrackName(track.getName());
		challengeDetail.setTrackLengthKm(track.getLengthKm());

		var car = Value.firstNonNull(challenge.getCar(), new Car());
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

	public static ChallengeSummaryResponse toSummaryResponse(ChallengeDetail challengeDetail) {
		Objects.requireNonNull(challengeDetail, "challengeDetail cannot be null in order to map its values");

		return new ChallengeSummaryResponse(
			challengeDetail.getId(),
			challengeDetail.getEndDate(),
			challengeDetail.getSimulatorType().getDisplayName(),
			challengeDetail.getBestParticipantName(),
			challengeDetail.getBestLapTime(),
			challengeDetail.getTrackCountry(),
			challengeDetail.getTrackName(),
			challengeDetail.getCarBrand(),
			challengeDetail.getCarName()
		);
	}
}
