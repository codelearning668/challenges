package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CarDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateCarRequest;
import sk.mkrajcovic.challenges.controller.mapper.CarMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchCarsCriteria;
import sk.mkrajcovic.challenges.service.CarService;

@RestController
@RequiredArgsConstructor
public class CarController {

	private final CarService service;

	@RolesAllowed(ADMIN)
	@PostMapping(path = "/cars", consumes = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createCar(@Valid @RequestBody CreateCarRequest request) {
		Integer carId = service.createCar(CarMapper.toCar(request));
		return CreatedResponseEntity.create("/cars/{carId}", carId);
	}

	@GetMapping(path = "/cars/{carId}", produces = APPLICATION_JSON_VALUE)
	CarDetailResponse getCar(@PathVariable @Positive Integer carId) {
		var car = service.getCar(carId);
		return CarMapper.toDetailResponse(car);
	}

	@GetMapping(path = "/cars", produces = APPLICATION_JSON_VALUE)
	List<CarDetailResponse> search(@ModelAttribute SearchCarsCriteria searchCriteria) {
		return service.searchCars(searchCriteria).stream()
			.map(CarMapper::toDetailResponse)
			.toList();
	}

}
