package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserRegistrationRequest {

	@NotBlank
	@Size(max = 100)
	private String username;

	@NotBlank
	@Size(max = 500)
	private String password;

}
