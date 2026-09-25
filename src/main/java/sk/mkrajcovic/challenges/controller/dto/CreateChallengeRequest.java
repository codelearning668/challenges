package sk.mkrajcovic.challenges.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import sk.mkrajcovic.challenges.enums.SimulatorType;

public record CreateChallengeRequest(

	@NotNull @Positive
	Integer trackId,

	@NotNull @Positive
	Integer carId,

	@NotNull @FutureOrPresent
	LocalDate endDate,

	@NotNull
	SimulatorType simulatorType

) { }
