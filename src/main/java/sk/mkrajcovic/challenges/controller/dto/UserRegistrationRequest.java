package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(

	@NotBlank
	@Size(max = 100)
	String username,

	@NotBlank
	@Size(max = 500)
	String password,

	@Email
	@Size(max = 50)
	@Pattern(regexp = "(?s).*\\S.*", message = "must not be blank when specified")
	String emailAddress

){ }
