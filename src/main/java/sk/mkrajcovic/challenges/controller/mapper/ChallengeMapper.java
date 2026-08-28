package sk.mkrajcovic.challenges.controller.mapper;

import sk.mkrajcovic.challenges.controller.dto.ChallengeDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.ParticipantDetailResponse;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.Participant;

public final class ChallengeMapper {

	public static ChallengeDetailResponse toDetailResponse(Challenge challenge) {
		var challengeDetail = new ChallengeDetailResponse();
		challengeDetail.setChallengeId(challenge.getId());
		challengeDetail.setChallengeEndDate(challenge.getEndDate());

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
		var participantDetail = new ParticipantDetailResponse();
		participantDetail.setParticipantId(participant.getId());
		participantDetail.setParticipantName(participant.getName());
		participantDetail.setParticipantBestLapTime(participant.getBestLapTime());

		return participantDetail;
	}
}
