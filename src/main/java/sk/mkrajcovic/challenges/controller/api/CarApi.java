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
import sk.mkrajcovic.challenges.controller.dto.CarDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.controller.dto.UpdateCarRequest;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchCarsCriteria;

@Tag(name = "Cars", description = "Services for browsing and administering cars available for challenges.")
public interface CarApi {

	@Operation(summary = "Retrieve car details", description = "Returns the selected car.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Car details.",
			content = @Content(schema = @Schema(implementation = CarDetailResponse.class), examples = @ExampleObject(value = """
			{
			  "id": 7,
			  "brand": "Ferrari",
			  "name": "488 GT3",
			  "horsePower": 550,
			  "torque": 700,
			  "wheelDrive": "REAR"
			}"""))
		),
		@ApiResponse(responseCode = "404", description = "No car exists with the supplied ID.")
	})
	CarDetailResponse getCar(@Positive Integer carId);

	@Operation(summary = "Search cars", description = "Returns cars matching the optional brand, name, horsepower, torque, and wheel-drive filters. Brand and name searches are case- and diacritic-insensitive.")
	@ApiResponse(
		responseCode = "200",
		description = "Cars matching the supplied criteria.",
		content = @Content(array = @ArraySchema(schema = @Schema(implementation = CarDetailResponse.class)), examples = @ExampleObject(value = """
		[
		  {
		    "id": 7,
		    "brand": "Ferrari",
		    "name": "488 GT3",
		    "horsePower": 550,
		    "torque": 700,
		    "wheelDrive": "REAR"
		  }
		]"""))
	)
	List<CarDetailResponse> search(SearchCarsCriteria searchCriteria);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Create a car",
		description = "Creates a car that can be selected for challenges. Requires the `ADMIN` role.",
		requestBody = @RequestBody(content = @Content(examples = @ExampleObject(value = """
		{
		  "brand": "Ferrari",
		  "name": "488 GT3",
		  "hp": 550,
		  "torque": 700,
		  "drive": "REAR"
		}""")))
	)
	@ApiResponse(responseCode = "201", description = "Car created. The Location header identifies the new car.")
	CreatedResponseEntity createCar(@Valid CreateCarRequest request);

	@Operation(
		security = @SecurityRequirement(name = "basicAuth"),
		summary = "Update a car",
		description = "Replaces the mutable properties of the selected car. Requires the `ADMIN` role.",
		requestBody = @RequestBody(content = @Content(examples = @ExampleObject(value = """
		{
		  "brand": "Ferrari",
		  "name": "488 GT3 Evo",
		  "hp": 550,
		  "torque": 700,
		  "drive": "REAR"
		}""")))
	)
	@ApiResponse(responseCode = "404", description = "No car exists with the supplied ID.")
	void updateCar(@Positive Integer carId, @Valid UpdateCarRequest request);
}

