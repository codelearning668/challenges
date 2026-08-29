package sk.mkrajcovic.challenges.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateChallengeRequest(

	@NotNull @Positive
	Integer trackId,

	@NotNull @Positive
	Integer carId,

	@NotNull @FutureOrPresent
	LocalDate endDate

) { }
