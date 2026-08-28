package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;
import sk.mkrajcovic.challenges.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PermitAll
	@PostMapping(path = "/public/register", consumes = APPLICATION_JSON_VALUE)
	void registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
		userService.registerNewUser(
			registrationRequest.getUsername(),
			registrationRequest.getPassword()
		);
	}
}
