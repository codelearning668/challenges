package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserEmailAddressUpdateRequest (

	@Email
	@Size(max = 50)
	@Pattern(regexp = "(?s).*\\S.*", message = "must not be blank when specified")
	String emailAddress
)
{ }
