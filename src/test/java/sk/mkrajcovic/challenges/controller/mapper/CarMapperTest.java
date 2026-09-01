package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.WheelDrive;
import sk.mkrajcovic.challenges.test.util.EntityTestUtils;

class CarMapperTest {

	@Test
	void shouldMapCarToDetailResponse() {
		var car = new Car();
		EntityTestUtils.setId(car, 42);
		car.setBrand("BMW");
		car.setName("M3");
		car.setHorsePower(510);
		car.setTorque(650);

		var response = CarMapper.toDetailResponse(car);

		assertAll(
			() -> assertEquals(42, response.id()),
			() -> assertEquals("BMW", response.brand()),
			() -> assertEquals("M3", response.name()),
			() -> assertEquals(510, response.horsePower()),
			() -> assertEquals(650, response.torque()));
	}

	@Test
	void shouldRejectNullCarWhenMappingToDetailResponse() {
		assertThrows(NullPointerException.class, () -> CarMapper.toDetailResponse(null));
	}

	@Test
	void shouldMapCreateCarRequestToCar() {
		var request = new CreateCarRequest("BMW", "M3", 510, 650, WheelDrive.ALL);
		var car = CarMapper.toCar(request);

		assertAll(
			() -> assertEquals("BMW", car.getBrand()),
			() -> assertEquals("M3", car.getName()),
			() -> assertEquals(510, car.getHorsePower()),
			() -> assertEquals(650, car.getTorque()),
			() -> assertEquals(WheelDrive.ALL, car.getWheelDrive()));
	}

	@Test
	void shouldRejectNullCreateCarRequest() {
		assertThrows(NullPointerException.class, () -> CarMapper.toCar(null));
	}
}
