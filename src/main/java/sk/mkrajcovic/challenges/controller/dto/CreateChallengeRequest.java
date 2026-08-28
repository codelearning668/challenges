package sk.mkrajcovic.challenges.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateChallengeRequest {

	@NotNull @Positive
	private Integer trackId;

	@NotNull @Positive
	private Integer carId;

	@NotNull @FutureOrPresent
	private LocalDate endDate;
}
