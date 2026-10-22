package sk.mkrajcovic.challenges.model.read;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A historical lap time and its signed change from the preceding recorded time.
 * A negative gap means improvement; a positive gap means regression.
 */
public record ParticipantTimelineEntry(
	Duration lapTime,
	LocalDateTime updatedAt,
	Duration signedGap
) { }
