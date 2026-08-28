package sk.mkrajcovic.challenges.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository.CarData;
import sk.mkrajcovic.challenges.security.UserRoles;
import sk.mkrajcovic.challenges.service.CarService;

@RestController
@RequiredArgsConstructor
public class CarController {

	private final CarService service;

	@RolesAllowed(UserRoles.ADMIN)
	@PostMapping(path = "/car", consumes = MediaType.APPLICATION_JSON_VALUE)
	CreatedResponseEntity createCar(@Valid @RequestBody CreateCarRequest request) {
		Integer carId = service.createCar(toCar(request));
		return CreatedResponseEntity.create("/car/{id}", carId);
	}

	// TODO: review - extract to mapper classes? (if will grow, here and elsewhere, only then)
	private Car toCar(CreateCarRequest request) {
		var car = new Car();
		car.setBrand(request.getBrand());
		car.setName(request.getName());
		car.setHorsePower(request.getHp());
		car.setTorque(request.getTorque());
		car.setWheelDrive(request.getDrive());
		return car;
	}

	@GetMapping(path = "/cars/", produces = MediaType.APPLICATION_JSON_VALUE)
	List<CarData> search() {
		return service.searchCars();
	}

}
