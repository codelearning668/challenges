package sk.mkrajcovic.challenges.controller.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.UpdateTrackRequest;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchTracksCriteria;

@Tag(name = "Tracks", description = "Services for browsing and administering racing tracks available for challenges.")
public interface TrackApi {

	@Operation(summary = "Retrieve track details", description = "Returns the selected racing track.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Track details.",
			content = @Content(schema = @Schema(implementation = TrackDetailResponse.class), examples = @ExampleObject(value = """
			{
			  "id": 4,
			  "country": "Italy",
			  "name": "Monza",
			  "lengthKm": 5.793
			}"""))
		),
		@ApiResponse(responseCode = "404", description = "No track exists with the supplied ID.")
	})
	TrackDetailResponse getTrack(@Positive Integer trackId);

	@Operation(summary = "Search tracks", description = "Returns tracks matching the optional country, name, and length filters. Country and name searches are case- and diacritic-insensitive.")
	@ApiResponse(
		responseCode = "200",
		description = "Tracks matching the supplied criteria.",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackDetailResponse.class)), examples = @ExampleObject(value = """
		[
		  {
		    "id": 4,
		    "country": "Italy",
		    "name": "Monza",
		    "lengthKm": 5.793
		  }
		]"""))
	)
	List<TrackDetailResponse> searchTracks(SearchTracksCriteria searchCriteria);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Create a track",
		description = "Creates a racing track that can be selected for challenges. Requires the `ADMIN` role.",
		requestBody = @RequestBody(content = @Content(examples = @ExampleObject(value = """
		{
		  "name": "Monza",
		  "country": "Italy",
		  "lengthKm": 5.793
		}""")))
	)
	@ApiResponse(responseCode = "201", description = "Track created. The Location header identifies the new track.")
	CreatedResponseEntity createTrack(@Valid CreateTrackRequest request);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Update a track",
		description = "Replaces the mutable properties of the selected track. Requires the `ADMIN` role.",
		requestBody = @RequestBody(content = @Content(examples = @ExampleObject(value = """
		{
		  "name": "Autodromo Nazionale Monza",
		  "country": "Italy",
		  "lengthKm": 5.793
		}""")))
	)
	@ApiResponse(responseCode = "404", description = "No track exists with the supplied ID.")
	void updateTrack(@Positive Integer trackId, @Valid UpdateTrackRequest request);
}

