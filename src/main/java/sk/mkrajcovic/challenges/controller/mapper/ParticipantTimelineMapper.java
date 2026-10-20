package sk.mkrajcovic.challenges.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.ParticipantTimelineResponse;
import sk.mkrajcovic.challenges.model.read.ParticipantTimelineEntry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipantTimelineMapper {

	public static ParticipantTimelineResponse toResponse(ParticipantTimelineEntry entry) {
		return new ParticipantTimelineResponse(
			entry.lapTime(),
			entry.updatedAt(),
			entry.signedGap()
		);
	}
}
