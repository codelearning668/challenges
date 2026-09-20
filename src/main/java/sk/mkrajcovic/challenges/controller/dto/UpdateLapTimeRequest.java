package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record UpdateLapTimeRequest(

	@NotBlank @Size(max = 100)
	String participantName,

	// can be null as we might want to discard it
	@Schema(
		description = "Lap time in m:ss.S, m:ss.SS, or m:ss.SSS format. Null clears the recorded lap time.",
		type = "string",
		pattern = "^\\d+:\\d{2}\\.\\d{1,3}$",
		example = "1:20.1",
		nullable = true
	)
	Duration newLapTime

) { }
