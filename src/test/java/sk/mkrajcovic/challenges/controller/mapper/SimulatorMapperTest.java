package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.enums.SimulatorType;
import sk.mkrajcovic.challenges.model.Simulator;
import sk.mkrajcovic.challenges.test.util.EntityTestUtils;

class SimulatorMapperTest {

	@Test
	void mapsSimulatorToDetailResponse() {
		var simulator = new Simulator();
		EntityTestUtils.setId(simulator, 1);
		simulator.setType(SimulatorType.ASSETTO_CORSA);

		var response = SimulatorMapper.toDetailResponse(simulator);

		assertAll(
			() -> assertEquals(1, response.id()),
			() -> assertEquals("Assetto Corsa", response.name())
		);
	}

	@Test
	void rejectsNullSimulatorWhenMappingToDetailResponse() {
		assertThrows(NullPointerException.class, () -> SimulatorMapper.toDetailResponse(null));
	}
}
