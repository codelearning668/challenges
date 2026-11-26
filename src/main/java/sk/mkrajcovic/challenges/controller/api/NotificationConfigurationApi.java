package sk.mkrajcovic.challenges.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationDetail;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationUpdateRequest;

@Tag(
	name = "Notification configuration",
	description = "Email-notification preferences of the authenticated user."
)
@SecurityRequirement(name = "basicAuth")
public interface NotificationConfigurationApi {

	@Operation(
		summary = "Retrieve current notification preferences",
		description = "Returns the complete email-notification preference configuration of the authenticated user."
	)
	@ApiResponse(
		responseCode = "200",
		description = "Current notification preferences.",
		content = @Content(
			schema = @Schema(implementation = NotificationConfigurationDetail.class),
			examples = @ExampleObject(value = """
				{
				  "notifyOnChallengeCreated": true,
				  "notifyOnChallengeClosed": false
				}
				""")
		)
	)
	NotificationConfigurationDetail getNotificationConfiguration();

	@Operation(
		summary = "Update current notification preferences",
		description = """
			Replaces every email-notification preference of the authenticated user.
			The request contains no username because users can update only their own
			configuration.
			""",
		requestBody = @RequestBody(content = @Content(
			schema = @Schema(implementation = NotificationConfigurationUpdateRequest.class),
			examples = @ExampleObject(value = """
				{
				  "notifyOnChallengeCreated": true,
				  "notifyOnChallengeClosed": false
				}
				""")
		))
	)
	@ApiResponse(responseCode = "204", description = "Notification preferences updated.")
	void updateNotificationConfiguration(@Valid NotificationConfigurationUpdateRequest request);
}
