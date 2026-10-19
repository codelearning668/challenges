package sk.mkrajcovic.challenges.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;

@Tag(name = "Conversation entries", description = "Administrative operations for independently identified challenge conversation entries.")
public interface ConversationApi {

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Delete a conversation entry",
		description = "Deletes the selected conversation entry. Any direct replies are preserved and become top-level entries. Requires the `ADMIN` role."
	)
	@ApiResponse(responseCode = "204", description = "Conversation entry deleted; no response body is returned.")
	@ApiResponse(responseCode = "400", description = "The entry ID is not positive.")
	@ApiResponse(responseCode = "401", description = "Authentication is required.")
	@ApiResponse(responseCode = "403", description = "The authenticated user does not have the `ADMIN` role.")
	@ApiResponse(responseCode = "404", description = "No conversation entry exists with the supplied ID.")
	void deleteConversationEntry(@Positive Integer entryId);

}
