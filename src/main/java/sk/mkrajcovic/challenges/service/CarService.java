package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.exception.BusinessViolation;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.read.CarDetail;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;
import sk.mkrajcovic.challenges.search.SearchCarsCriteria;
import sk.mkrajcovic.challenges.util.Text;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarRepository repository;
	private final ChallengeRepository challengeRepo;

	@Transactional
	public Integer createCar(Car car) {
		return repository.save(car).getId();
	}

	public Car getCar(Integer carId) {
		return EntityUtils.getExistingEntityById(repository, carId);
	}

	public List<CarDetail> searchCars(SearchCarsCriteria searchCriteria) {
		normalizeSearchCriteria(searchCriteria);
		return repository.findCars(searchCriteria);
	}

	/*
	 * Mutation is intentional because the criteria object is passed to the
	 * repository afterwards and have no other usage really.
	 */
	private void normalizeSearchCriteria(SearchCarsCriteria criteria) {
		criteria.setBrand(Text.normalizeForSearch(criteria.getBrand()));
		criteria.setName(Text.normalizeForSearch(criteria.getName()));
	}


	@Transactional
	public void updateCar(Integer carId, Car carToSave){
		var car = getCar(carId);

		car.setBrand(carToSave.getBrand());
		car.setName(carToSave.getName());
		car.setHorsePower(carToSave.getHorsePower());
		car.setTorque(carToSave.getTorque());
		car.setWheelDrive(carToSave.getWheelDrive());

		repository.save(car);
	}

	@Transactional
	public void deleteCar(Integer carId){
		var car = getCar(carId);

		if(!verifyCarIsNotAlreadyAssignedToChallenge(carId)){
			throw new BusinessViolation("carOrTrackAlreadyAssigned");
		}

		repository.delete(car);
	}

	private boolean verifyCarIsNotAlreadyAssignedToChallenge(Integer carId){
		return !challengeRepo.existsByCarId(carId);
	}
}
