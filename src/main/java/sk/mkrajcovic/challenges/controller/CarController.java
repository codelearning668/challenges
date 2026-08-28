package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.controller.mapper.CarMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository.CarData;
import sk.mkrajcovic.challenges.service.CarService;

@RestController
@RequiredArgsConstructor
public class CarController {

	private final CarService service;

	@RolesAllowed(ADMIN)
	@PostMapping(path = "/car", consumes = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createCar(@Valid @RequestBody CreateCarRequest request) {
		Integer carId = service.createCar(CarMapper.toCar(request));
		return CreatedResponseEntity.create("/car/{id}", carId);
	}

	@GetMapping(path = "/cars/", produces = APPLICATION_JSON_VALUE)
	List<CarData> search() {
		return service.searchCars();
	}

}
