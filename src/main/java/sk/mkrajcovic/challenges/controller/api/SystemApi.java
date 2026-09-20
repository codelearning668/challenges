package sk.mkrajcovic.challenges.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import sk.mkrajcovic.challenges.controller.dto.UserInfoResponse;

@Tag(name = "System", description = "Services exposing information about the system availability, environment, the authenticated user etc.")
public interface SystemApi {

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Retrieve authenticated user information", description = "Returns the username and authorization roles of the authenticated user.")
	@ApiResponse(
		responseCode = "200",
		description = "Authenticated user information.",
		content = @Content(schema = @Schema(implementation = UserInfoResponse.class), examples = @ExampleObject(value = """
		{
		  "username": "racer1",
		  "roles": ["PARTICIPANT"]
		}"""))
	)
	UserInfoResponse getUserInfo();
}

