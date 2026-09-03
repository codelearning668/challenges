package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
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
			track.getLengthKm()
		);
	}

	public static TrackDetailResponse toDetailResponse(TrackDetail track) {
		Objects.requireNonNull(track, "trackDetail cannot be null in order to map its values");

		return new TrackDetailResponse(
			track.getId(),
			track.getCountry(),
			track.getName(),
			track.getLengthKm()
		);
	}

	public static Track toTrack(CreateTrackRequest trackRequest) {
		Objects.requireNonNull(trackRequest, "input request cannot be null in order to map its values");

		var track = new Track();
		track.setCountry(trackRequest.country());
		track.setName(trackRequest.name());
		track.setLengthKm(trackRequest.lengthKm());

		return track;
	}

}
