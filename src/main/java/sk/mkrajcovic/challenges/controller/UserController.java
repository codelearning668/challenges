package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.controller.api.UserApi;
import sk.mkrajcovic.challenges.controller.dto.UserEmailAddressUpdateRequest;
import sk.mkrajcovic.challenges.controller.dto.UserInfoResponse;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;
import sk.mkrajcovic.challenges.controller.mapper.UserMapper;
import sk.mkrajcovic.challenges.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
class UserController implements UserApi {

	private final UserService userService;
	private final CallContext callContext;

	@GetMapping(path = "/info", produces = APPLICATION_JSON_VALUE)
	public UserInfoResponse getUserInfo() {
		return UserMapper.toDetailResponse(callContext.getCurrentUser());
	}

	@PermitAll
	@PostMapping(path = "/register", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
		userService.registerNewUser(
			registrationRequest.username(),
			registrationRequest.password(),
			registrationRequest.emailAddress()
		);
	}

	@PatchMapping(path = "/email-address", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateUserEmailAddress(@Valid @RequestBody UserEmailAddressUpdateRequest updateEmailRequest) {
		userService.updateUserEmailAddress(updateEmailRequest.emailAddress());
	}
}
