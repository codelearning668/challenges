package sk.mkrajcovic.challenges.controller.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateTrackRequest(

	@NotBlank @Size(max = 100)
	String name,

	@Size(max = 100)
	String country,

	@Positive
	@Digits(integer = 7, fraction = 3)
	BigDecimal lengthKm

) { }