package sk.mkrajcovic.challenges.controller.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
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
	List<ChallengeSummaryResponse> searchChallenges(SearchChallengesCriteria searchCriteria);

	@Operation(summary = "Retrieve challenge details", description = "Returns the selected challenge, its car and track, the current leaderboard, and all registered participants.")
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
				  ]
				}""")
			)
	)
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
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
	@ApiResponse(responseCode = "201", description = "Challenge created. The Location header identifies the new challenge.")
	@ApiResponse(responseCode = "404", description = "The selected car or track does not exist.")
	@ApiResponse(responseCode = "409", description = "An active challenge already exists for the selected car and track.")
	@ApiResponse(responseCode = "422", description = "The submitted end date does not satisfy the challenge rules.")
	CreatedResponseEntity createChallenge(@Valid CreateChallengeRequest challenge);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Register for a challenge", description = "Registers the authenticated participant for an active challenge. A participant cannot register twice and needs a previous win before joining multiple active challenges. Requires the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "204", description = "Participant registered for the challenge.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is already registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed, or the participant is not eligible to join multiple active challenges.")
	void registerForChallenge(@Positive Integer challengeId);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Quit a challenge", description = "Removes the authenticated participant from an active challenge. Requires the `PARTICIPANT` role.")
	@ApiResponse(responseCode = "204", description = "Participant removed from the challenge.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "409", description = "The authenticated participant is not registered for the challenge.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed.")
	void quitChallenge(@Positive Integer challengeId);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Update a participant lap time",
		description = "Records or removes a registered participant's best lap time. A participant may update only their own time while the challenge is active. An administrator may update any registered participant, including after the challenge closes. Requires the `PARTICIPANT` or `ADMIN` role.",
		requestBody = @RequestBody(
			description = "The registered participant and their new lap time. Set newLapTime to null to clear the recorded time.",
			content = @Content(examples = @ExampleObject(value = """
			{
			  "participantName": "racer1",
			  "newLapTime": "1:42.537"
			}"""))
	)
	)
	@ApiResponse(responseCode = "204", description = "Participant lap time updated.")
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
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed or the submitted end date is invalid.")
	void updateChallengeEndDate(@Positive Integer challengeId, @Valid UpdateChallengeEndDateRequest request);

	@Operation(security = @SecurityRequirement(name = "basicAuth"), summary = "Delete a challenge", description = "Deletes an active challenge. Closed challenges cannot be deleted. Requires the `ADMIN` role.")
	@ApiResponse(responseCode = "204", description = "Challenge deleted.")
	@ApiResponse(responseCode = "404", description = "No challenge exists with the supplied ID.")
	@ApiResponse(responseCode = "422", description = "The challenge is closed.")
	void deleteChallenge(@Positive Integer challengeId);
}


