package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.UpdateTrackRequest;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.TrackDetail;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TrackMapper {

	public static TrackDetailResponse toDetailResponse(Track track) {
		Objects.requireNonNull(track, "track cannot be null in order to map its values");

		return new TrackDetailResponse(
			track.getId(),
			track.getCountry(),
			track.getName(),
			track.getLengthKm(),
			track.getSimulator().getType().getDisplayName(),
			track.isFromDlc()
		);
	}

	public static TrackDetailResponse toDetailResponse(TrackDetail track) {
		Objects.requireNonNull(track, "trackDetail cannot be null in order to map its values");

		return new TrackDetailResponse(
			track.getId(),
			track.getCountry(),
			track.getName(),
			track.getLengthKm(),
			track.getSimulatorType().getDisplayName(),
			track.getFromDlc()
		);
	}

	public static Track toTrack(CreateTrackRequest createRequest) {
		Objects.requireNonNull(createRequest, "input request cannot be null in order to map its values");

		var track = new Track();
		track.setCountry(createRequest.country());
		track.setName(createRequest.name());
		track.setLengthKm(createRequest.lengthKm());
		track.setFromDlc(createRequest.fromDlc());

		return track;
	}

	public static Track toTrack(UpdateTrackRequest updateRequest) {
		Objects.requireNonNull(updateRequest, "input request cannot be null in order to map its values");

		var track = new Track();
		track.setCountry(updateRequest.country());
		track.setName(updateRequest.name());
		track.setLengthKm(updateRequest.lengthKm());
		track.setFromDlc(updateRequest.fromDlc());

		return track;
	}

}
