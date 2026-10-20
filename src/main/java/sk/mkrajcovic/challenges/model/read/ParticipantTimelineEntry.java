package sk.mkrajcovic.challenges.model.read;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A historical lap time and its signed change from the preceding recorded time.
 * A positive change means improvement; a negative change means regression.
 */
public record ParticipantTimelineEntry(
	Duration lapTime,
	LocalDateTime updatedAt,
	Duration signedGap
) { }
