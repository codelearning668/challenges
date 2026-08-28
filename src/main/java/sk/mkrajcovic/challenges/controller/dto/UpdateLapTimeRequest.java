package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLapTimeRequest {

	@NotBlank @Size(max = 100)
	private String participantName;

	// can be null as we might want to discard it
	private Duration newLapTime;
}
