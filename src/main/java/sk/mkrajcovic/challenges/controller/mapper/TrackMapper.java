package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
import sk.mkrajcovic.challenges.model.Track;

public final class TrackMapper {

	public static TrackDetailResponse toDetailResponse(Track track) {
		Objects.requireNonNull(track, "track cannot be null in order to map its values");

		var trackDetail = new TrackDetailResponse();
		trackDetail.setId(track.getId());
		trackDetail.setCountry(track.getCountry());
		trackDetail.setName(track.getName());
		trackDetail.setLengthKm(track.getLengthKm());

		return trackDetail;
	}

	public static Track toTrack(CreateTrackRequest request) {
		Objects.requireNonNull(request, "input request cannot be null in order to map its values");

		var track = new Track();
		track.setCountry(request.getCountry());
		track.setName(request.getName());
		track.setLengthKm(request.getLengthKm());

		return track;
	}

}
