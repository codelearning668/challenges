package sk.mkrajcovic.challenges.controller.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import sk.mkrajcovic.challenges.controller.dto.ChallengeDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.ChallengeSummaryResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateChallengeRequest;
import sk.mkrajcovic.challenges.controller.dto.CreateConversationEntryRequest;
import sk.mkrajcovic.challenges.controller.dto.UpdateChallengeEndDateRequest;
import sk.mkrajcovic.challenges.controller.dto.UpdateLapTimeRequest;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchChallengesCriteria;

@Tag(name = "Challenges", description = "Services for browsing and administering racing-simulator challenges, including participant registration and lap-time results.")
public interface ChallengeApi {

	@Operation(
		summary = "Search challenges",
		description = "Returns challenges matching the optional simulator, end-date, leaderboard, track, and car filters. The simulatorId identifies the simulator associated with both the selected car and track. Searches by text fields are case- and diacritic-insensitive."
	)
	@ApiResponse(
		responseCode = "200",
		description = "Challenges matching the supplied criteria.",
		content = @Content(
			array = @ArraySchema(schema = @Schema(implementation = ChallengeSummaryResponse.class)),
			examples = @ExampleObject(value = """
			[
			  {
			    "challengeId": 12,
			    "challengeEndDate": "2026-10-31",
			    "simulatorName": "Assetto Corsa",
			    "bestParticipantName": "racer1",
			    "bestLapTime": "01:42.537",
			    "trackCountry": "Italy",
			    "trackName": "Monza",
			    "carBrand": "Ferrari",
			    "carName": "488 GT3"
			  }
			]""")
		)
	)
	@ApiResponse(
		responseCode = "400",
		description = "Invalid search criteria."
	)
	List<ChallengeSummaryResponse> searchChallenges(SearchChallengesCriteria searchCriteria);

	@Operation(summary = "Retrieve challenge details", description = "Returns the selected challenge, its car and track, the current leaderboard, registered participants, and conversation entries in chronological order from earliest to latest.")
	@ApiResponse(
		responseCode = "200",
		description = "Challenge details.",
		content = @Content(
			schema = @Schema(implementation = ChallengeDetailResponse.class),
			examples = @ExampleObject(value = """
			{
			  "challengeId": 12,
			  "challengeEndDate": "2028-10-31",
			  "simulatorName": "Assetto Corsa",
			  "bestParticipantName": "racer1",
			  "bestLapTime": "01:42.537",
			  "trackId": 4,
			  "trackName": "Monza",
			  "trackCountry": "Italy",
			  "trackLengthKm": 5.793,
			  "carId": 7,
			  "carBrand": "Ferrari",
			  "carName": "488 GT3",
			  "carHorsePower": 550,
			  "carTorque": 700,
			  "participants": [
			    {
			      "participantId": 31,
			      "participantName": "racer1",
			      "participantBestLapTime": "01:42.537"
			    }
			  ],
			  "conversation": [
			    {
			      "entryId": 81,
			      "parentEntryId": null,
			      "message": "Good luck everyone!",
			      "participantName": "racer1",
			      "participantLapTimeAtPosting": "01:42.537",
			      "createdAt": "2026-10-19T14:32:10"
			    },
			    {
			      "entryId": 82,
			      "parentEntryId": 81,
			      "message": "Thanks, you too!",
			      "participantName": "racer2",
			      "participantLapTimeAtPosting": null,
			      "createdAt": "2026-10-19T14:35:42"
			    }
			  ]
			}""")
		)
	)
	@ApiResponse(
		responseCode = "400",
		description = "Invalid path parameter.",
		content = @Content
	)
	@ApiResponse(
		responseCode = "404",
		description = "No challenge exists with the supplied ID.",
		content = @Content
	)
	ChallengeDetailResponse getChallenge(@Positive Integer challengeId);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Create a challenge",
		description = "Creates an active challenge for the selected car and track. Both must belong to the same simulator, which the challenge derives from them. Only one active challenge may exist for the same car-and-track combination. Requires the `ADMIN` role.",
		requestBody = @RequestBody(
			description = "The car, track, and inclusive challenge end date.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "trackId": 4,
			  "carId": 7,
			  "endDate": "2028-10-31"
			}"""))
		)
	)
	@ApiResponse(
		responseCode = "201",
		description = "Challenge created.",
		headers = @Header(name = "Location", description = "URI of the created challenge, `/challenges/{challengeId}`.", schema = @Schema(type = "string", format = "uri"))
	)
	@ApiResponse(responseCode = "400", description = "Invalid request.")
	@ApiResponse(responseCode = "404", description = "The selected car or track does not exist.")
	@ApiResponse(responseCode = "409", description = "An active challenge already exists for the selected car and track.")
	@ApiResponse(responseCode = "422", description = "The submitted end date does not satisfy the challenge rules.")
	CreatedResponseEntity createChallenge(@Valid CreateChallengeRequest challenge);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Register for a challenge", description = "Registers the authenticated participant for an active challenge. A participant cannot register twice and needs a previous win before joining multiple active challenges. Requires the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "204", description = "Participant registered for the challenge.")
	@ApiResponse(responseCode = "400", description = "Invalid path parameter.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is already registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed, or the participant is not eligible to join multiple active challenges.")
	void registerForChallenge(@Positive Integer challengeId);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Quit a challenge", description = "Removes the authenticated participant from an active challenge. Requires the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "204", description = "Participant removed from the challenge.")
	@ApiResponse(responseCode = "400", description = "Invalid path parameter.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is not registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed.")
	void quitChallenge(@Positive Integer challengeId);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Update a participant lap time",
		description = "Records a registered participant's lap time. Participants may update only their own time while the challenge is active; they must provide a time, and each update must be strictly faster than their previous time. A participant with no recorded time may set their first time. Administrators may update any registered participant's time, including after the challenge closes; they may clear a time or set a slower time to correct results for rule violations or changed conditions. Requires the `PARTICIPANT` or `ADMIN` role.",
		requestBody = @RequestBody(
			description = "The registered participant and new lap time. Participants must provide a strictly faster time (or any valid first time if none is recorded). Administrators may set the time to null to clear it or provide a slower time.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "participantName": "racer1",
			  "newLapTime": "1:42.537"
			}"""))
		)
	)
	@ApiResponse(responseCode = "204", description = "Participant lap time updated.")
	@ApiResponse(responseCode = "400", description = "Invalid request or path parameter.")
	@ApiResponse(responseCode = "403", description = "A non-administrator attempted to update another participant's lap time.")
	@ApiResponse(responseCode = "404", description = "The participant is not registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "A non-administrator attempted to update a lap time after the challenge closed.")
	void updateLapTime(@Positive Integer challengeId, @Valid UpdateLapTimeRequest request);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Change a challenge end date",
		description = "Changes the inclusive end date of an active challenge. Closed challenges cannot be changed. Requires the `ADMIN` role.",
		requestBody = @RequestBody(
			description = "The new inclusive challenge end date.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "endDate": "2028-11-07"
			}"""))
		)
	)
	@ApiResponse(responseCode = "204", description = "Challenge end date updated.")
	@ApiResponse(responseCode = "400", description = "Invalid request or path parameter.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed or the submitted end date is invalid.")
	void updateChallengeEndDate(@Positive Integer challengeId, @Valid UpdateChallengeEndDateRequest request);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Delete a challenge", description = "Deletes an active challenge. Closed challenges cannot be deleted. Requires the `ADMIN` role.")
	@ApiResponse(responseCode = "204", description = "Challenge deleted.")
	@ApiResponse(responseCode = "400", description = "Invalid path parameter.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed.")
	void deleteChallenge(@Positive Integer challengeId);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Post a conversation entry",
		description = "Creates a top-level conversation entry in an active challenge for the authenticated registered participant. The participant name and best lap time are captured with the entry as historical values. Requires the `PARTICIPANT` role.",
		requestBody = @RequestBody(
			description = "The conversation message, up to 1000 characters.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "message": "Good luck everyone!"
			}"""))
		)
	)
	@ApiResponse(
		responseCode = "201",
		description = "Conversation entry created.",
		headers = @Header(name = "Location", description = "URI of the created conversation entry, `/conversation-entries/{entryId}`.", schema = @Schema(type = "string", format = "uri"))
	)
	@ApiResponse(responseCode = "400", description = "Invalid request or path parameter.")
	@ApiResponse(responseCode = "401", description = "Authentication is required.")
	@ApiResponse(responseCode = "403", description = "The authenticated user does not have the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is not registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed.")
	CreatedResponseEntity addConversationEntry(
		@Parameter(description = "ID of the challenge receiving the conversation entry.") @Positive Integer challengeId,
		@Valid CreateConversationEntryRequest request);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Reply to a conversation entry",
		description = "Creates a reply to an entry in the same active challenge. The authenticated participant must be registered for the challenge. The participant name and best lap time are captured with the reply as historical values. Requires the `PARTICIPANT` role.",
		requestBody = @RequestBody(
			description = "The reply message, up to 1000 characters.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "message": "Thanks, looking forward to it!"
			}"""))
		)
	)
	@ApiResponse(
		responseCode = "201",
		description = "Reply created.",
		headers = @Header(name = "Location", description = "URI of the created conversation entry, `/conversation-entries/{entryId}`.", schema = @Schema(type = "string", format = "uri"))
	)
	@ApiResponse(responseCode = "400", description = "Invalid request or path parameter.")
	@ApiResponse(responseCode = "401", description = "Authentication is required.")
	@ApiResponse(responseCode = "403", description = "The authenticated user does not have the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is not registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed, or the parent entry does not belong to this challenge.")
	CreatedResponseEntity addConversationEntry(
		@Parameter(description = "ID of the challenge receiving the reply.") @Positive Integer challengeId,
		@Parameter(description = "ID of the conversation entry being replied to.") @Positive Integer parentEntryId,
		@Valid CreateConversationEntryRequest request);
}
