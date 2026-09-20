package sk.mkrajcovic.challenges.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import sk.mkrajcovic.challenges.controller.dto.UserRegistrationRequest;

@Tag(name = "Users", description = "Services for registering application users.")
public interface UserApi {

	@Operation(
		summary = "Register a user",
		description = "Creates an enabled user authorized as `PARTICIPANT`. The username must be unique. This operation is public.",
		requestBody = @RequestBody(content = @Content(examples = @ExampleObject(value = """
		{
		  "username": "racer1",
		  "password": "choose-a-strong-password"
		}""")))
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "User registered and authorized as `PARTICIPANT`."),
		@ApiResponse(responseCode = "409", description = "The supplied username is already in use.")
	})
	void registerUser(@Valid UserRegistrationRequest registrationRequest);
}
