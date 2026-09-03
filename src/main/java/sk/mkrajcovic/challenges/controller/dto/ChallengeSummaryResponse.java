package sk.mkrajcovic.challenges.controller.dto;

import java.time.LocalDate;

public record ChallengeSummaryResponse(

	Integer challengeId,
	LocalDate challengeEndDate,

	String trackCountry,
	String trackName,

	String carBrand,
	String carName

) { }
