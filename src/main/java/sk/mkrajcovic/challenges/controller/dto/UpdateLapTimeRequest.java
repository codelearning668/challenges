package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateLapTimeRequest(

	@NotBlank @Size(max = 100)
	String participantName,

	// can be null as we might want to discard it
	Duration newLapTime

) { }
