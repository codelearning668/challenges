package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record ConversationEntryDetailResponse(

	@Schema(description = "Unique identifier of the conversation entry.", example = "81")
	Integer entryId,

	@Schema(description = "ID of the parent entry for a reply; null for a top-level entry.", example = "80", nullable = true)
	Integer parentEntryId,

	@Schema(description = "Message content posted by the participant.", example = "Good luck everyone!")
	String message,

	@Schema(description = "Participant name captured when the entry was posted.", example = "racer1")
	String participantName,

	@Schema(
		description = "Participant's best lap time captured when the entry was posted, if available; serialized in canonical mm:ss.SSS format.",
		type = "string",
		pattern = "^\\d{2}:\\d{2}\\.\\d{3}$",
		example = "01:42.537",
		nullable = true
	)
	Duration participantLapTimeAtPosting,

	@Schema(description = "Local date-time when the entry was created.", example = "2026-10-19T14:32:10")
	LocalDateTime createdAt

) { }
