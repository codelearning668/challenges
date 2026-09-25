package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.api.SimulatorApi;
import sk.mkrajcovic.challenges.controller.dto.SimulatorDetailResponse;
import sk.mkrajcovic.challenges.controller.mapper.SimulatorMapper;
import sk.mkrajcovic.challenges.service.SimulatorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/simulators")
class SimulatorController implements SimulatorApi {

	private final SimulatorService simulatorService;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<SimulatorDetailResponse> getSimulators() {
		return simulatorService.getSimulators().stream()
			.map(SimulatorMapper::toDetailResponse)
			.toList();
	}
}
