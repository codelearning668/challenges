package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipantTimelineResponse(
	@Schema(
		description = "Historical lap time in canonical mm:ss.SSS format.",
		type = "string",
		pattern = "^\\d{2}:\\d{2}\\.\\d{3}$",
		example = "01:42.537"
	)
	Duration lapTime,

	@Schema(description = "Time when this historical lap time was recorded.", example = "2026-10-19T14:32:10")
	LocalDateTime updatedAt,

	@Schema(
		description = "Signed lap-time change from the preceding historical time, calculated as current lap time minus previous lap time. Negative means improvement; positive means regression. The earliest historical time has no predecessor and therefore a zero gap.",
		type = "string",
		pattern = "^-?\\d{2}:\\d{2}\\.\\d{3}$",
		example = "-00:01.250"
	)
	Duration gap
) {
}
