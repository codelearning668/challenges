package sk.mkrajcovic.challenges.controller.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import sk.mkrajcovic.challenges.controller.dto.ParticipantTimelineResponse;

@Tag(name = "Participants", description = "Participant lap-time history and progress.")
public interface ParticipantApi {

	@Operation(
		summary = "Retrieve a participant's lap-time history",
		description = "Returns historical lap times newest-first. Each gap is the signed change from the preceding historical time (current minus previous): a negative gap means improvement and a positive gap means regression. The earliest historical time has a zero gap."
	)
	@ApiResponse(
		responseCode = "200",
		description = "Historical lap times, newest first.",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParticipantTimelineResponse.class)))
	)
	@ApiResponse(responseCode = "400", description = "The participant ID is invalid.")
	List<ParticipantTimelineResponse> getParticipantTimeline(
		@Parameter(description = "ID of the participant.") @Positive Integer participantId
	);
}
