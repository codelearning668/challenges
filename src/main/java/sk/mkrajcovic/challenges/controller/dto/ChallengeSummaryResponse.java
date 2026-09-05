package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;
import java.time.LocalDate;

public record ChallengeSummaryResponse(

	Integer challengeId,
	LocalDate challengeEndDate,
	String bestParticipantName,
	Duration bestLapTime,

	String trackCountry,
	String trackName,

	String carBrand,
	String carName

) { }
