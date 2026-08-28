package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.Car;

public final class CarMapper {

	public static Car toCar(CreateCarRequest request) {
		Objects.requireNonNull(request);

		var car = new Car();
		car.setBrand(request.getBrand());
		car.setName(request.getName());
		car.setHorsePower(request.getHp());
		car.setTorque(request.getTorque());
		car.setWheelDrive(request.getDrive());

		return car;
	}

}
