package sk.mkrajcovic.challenges.controller.api;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import sk.mkrajcovic.challenges.controller.dto.SimulatorDetailResponse;

@Tag(name = "Simulators", description = "Supported racing simulators used to classify cars, tracks, and challenges.")
public interface SimulatorApi {

	@Operation(
		summary = "List simulators",
		description = "Returns the supported simulators. Use the returned ID as simulatorId when creating a car or track."
	)
	@ApiResponse(
		responseCode = "200",
		description = "Supported simulators.",
		content = @Content(
			array = @ArraySchema(schema = @Schema(implementation = SimulatorDetailResponse.class)),
			examples = @ExampleObject(value = """
				[
				  {
				    "id": 1,
				    "name": "Assetto Corsa"
				  },
				  {
				    "id": 2,
				    "name": "Assetto Corsa Competizione"
				  },
				  {
				    "id": 3,
				    "name": "WRC Generations"
				  }
				]
				""")
		)
	)
	List<SimulatorDetailResponse> getSimulators();
}
