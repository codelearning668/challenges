package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository.CarData;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarRepository repository;

	@Transactional
	public Integer createCar(Car car) {
		return repository.save(car).getId();
	}

	public Car getCar(Integer carId) {
		return EntityUtils.getExistingEntityById(repository, carId);
	}

	// TODO: implement searchCriteria
	public List<CarData> searchCars() {
		return repository.findCarsByParams();
	}

}
