package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

	@NotBlank
	@Size(max = 100)
	String username,

	@NotBlank
	@Size(max = 500)
	String password

){ }
