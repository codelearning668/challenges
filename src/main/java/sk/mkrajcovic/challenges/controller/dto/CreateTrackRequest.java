package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTrackRequest {

	@NotBlank @Size(max = 100)
	private String name;

	@Size(max = 100)
	private String country;

	@Positive
	private Double lengthKm;
}
