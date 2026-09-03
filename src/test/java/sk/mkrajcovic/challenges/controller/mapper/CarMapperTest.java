package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.WheelDrive;
import sk.mkrajcovic.challenges.model.read.CarDetail;
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
		assertThrows(NullPointerException.class, () -> CarMapper.toDetailResponse((Car) null));
	}

	@Test
	void shouldMapCarDetailToDetailResponse() {
		var carDetail = new CarDetail() {

			@Override
			public Integer getId() {
				return 42;
			}

			@Override
			public String getBrand() {
				return "BMW";
			}

			@Override
			public String getName() {
				return "M3";
			}

			@Override
			public Integer getHorsePower() {
				return 510;
			}

			@Override
			public Integer getTorque() {
				return 650;
			}

			@Override
			public String getWheelDrive() {
				return "ALL";
			}
		};

		var response = CarMapper.toDetailResponse(carDetail);

		assertAll(
			() -> assertEquals(42, response.id()),
			() -> assertEquals("BMW", response.brand()),
			() -> assertEquals("M3", response.name()),
			() -> assertEquals(510, response.horsePower()),
			() -> assertEquals(650, response.torque()));
	}

	@Test
	void shouldRejectNullCarDetailWhenMappingToDetailResponse() {
		var exception = assertThrows(
			NullPointerException.class,
			() -> CarMapper.toDetailResponse((CarDetail) null));

		assertEquals("carDetail cannot be null in order to map its values", exception.getMessage());
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
