package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ParticipantDetailResponse(
	Integer participantId,
	String participantName,
	Duration participantBestLapTime,

	@JsonInclude(JsonInclude.Include.NON_NULL)
	Integer version) {

	/**
	 * Creates a participant response without a version.
	 * <p>
	 * Use this constructor when the participant version is not relevant to the
	 * response. The version field will be omitted from JSON serialization.
	 */
	public ParticipantDetailResponse(
			Integer participantId,
			String participantName,
			Duration participantBestLapTime) {

		this(participantId, participantName, participantBestLapTime, null);
	}
}
