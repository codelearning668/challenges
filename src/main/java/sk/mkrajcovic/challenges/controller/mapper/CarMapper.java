package sk.mkrajcovic.challenges.controller.mapper;

import java.util.Objects;

import sk.mkrajcovic.challenges.controller.dto.CarDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.model.Car;

public final class CarMapper {

	public static CarDetailResponse toDetailResponse(Car car) {
		Objects.requireNonNull(car, "car cannot be null in order to map its values");

		var carDetail = new CarDetailResponse();
		carDetail.setId(car.getId());
		carDetail.setBrand(car.getBrand());
		carDetail.setName(car.getName());
		carDetail.setHorsePower(car.getHorsePower());
		carDetail.setTorque(car.getTorque());

		return carDetail;
	}

	public static Car toCar(CreateCarRequest request) {
		Objects.requireNonNull(request, "input request cannot be null in order to map its values");

		var car = new Car();
		car.setBrand(request.getBrand());
		car.setName(request.getName());
		car.setHorsePower(request.getHp());
		car.setTorque(request.getTorque());
		car.setWheelDrive(request.getDrive());

		return car;
	}

}
