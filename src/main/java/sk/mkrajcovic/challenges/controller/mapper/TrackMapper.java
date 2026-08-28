package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.model.Track;

public final class TrackMapper {

	public static Track toTrack(CreateTrackRequest request) {
		Objects.requireNonNull(request, "input request cannot be null in order to map its properties");

		var track = new Track();
		track.setCountry(request.getCountry());
		track.setName(request.getName());
		track.setLengthKm(request.getLengthKm());

		return track;
	}

}
