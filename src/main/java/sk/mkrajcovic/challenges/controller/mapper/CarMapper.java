package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CarDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.Car;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CarMapper {

	public static CarDetailResponse toDetailResponse(Car car) {
		Objects.requireNonNull(car, "car cannot be null in order to map its values");

		return new CarDetailResponse(
			car.getId(),
			car.getBrand(),
			car.getName(),
			car.getHorsePower(),
			car.getTorque()
		);
	}

	public static Car toCar(CreateCarRequest createRequest) {
		Objects.requireNonNull(createRequest, "input request cannot be null in order to map its values");

		var car = new Car();
		car.setBrand(createRequest.brand());
		car.setName(createRequest.name());
		car.setHorsePower(createRequest.hp());
		car.setTorque(createRequest.torque());
		car.setWheelDrive(createRequest.drive());

		return car;
	}

}
