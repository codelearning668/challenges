package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTrackRequest(

	@NotBlank @Size(max = 100)
	String name,

	@Size(max = 100)
	String country,

	@Positive
	Double lengthKm

) { }
