package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.SimulatorDetailResponse;
import sk.mkrajcovic.challenges.model.Simulator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SimulatorMapper {

	public static SimulatorDetailResponse toDetailResponse(Simulator simulator) {
		Objects.requireNonNull(simulator, "simulator cannot be null in order to map its values");

		return new SimulatorDetailResponse(
			simulator.getId(),
			simulator.getType().getDisplayName()
		);
	}
}
