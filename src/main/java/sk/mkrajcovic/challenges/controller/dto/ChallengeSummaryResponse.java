package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChallengeSummaryResponse(

	Integer challengeId,
	LocalDate challengeEndDate,
	String bestParticipantName,

	@Schema(
		description = "Quickest recorded lap time, returned in canonical mm:ss.SSS format.",
		type = "string",
		pattern = "^\\d{2}:\\d{2}\\.\\d{3}$",
		example = "01:20.100",
		nullable = true
	)
	Duration bestLapTime,

	String trackCountry,
	String trackName,

	String carBrand,
	String carName

) { }
