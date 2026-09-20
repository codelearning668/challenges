package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipantDetailResponse(

	Integer participantId,
	String participantName,

	@Schema(
		description = "Participant's quickest recorded lap time, returned in canonical mm:ss.SSS format.",
		type = "string",
		pattern = "^\\d{2}:\\d{2}\\.\\d{3}$",
		example = "01:20.100",
		nullable = true
	)
	Duration participantBestLapTime

) { }
