package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.read.CarDetail;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;
import sk.mkrajcovic.challenges.search.SearchCarsCriteria;
import sk.mkrajcovic.challenges.util.Text;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarRepository repository;
	private final SimulatorService simulatorService;

	/**
	 * Creates a new car and associates it with the specified simulator.
	 *
	 * @param car car to create
	 * @param simulatorId identifier of the simulator associated with the car
	 * @return identifier of the newly created car
	 */
	@Transactional
	public Integer createCar(Car car, Integer simulatorId) {
		car.setSimulator(simulatorService.getSimulator(simulatorId));
		return repository.save(car).getId();
	}

	/**
	 * Returns the car by the specified identifier.
	 *
	 * @param carId identifier of the car to retrieve
	 * @return the car with the specified identifier
	 * @throws ResourceNotFound if no car with the specified identifier exists
	 */
	public Car getCar(Integer carId) {
		return EntityUtils.getExistingEntityById(repository, carId);
	}

	/**
	 * Searches for cars matching the specified search criteria.
	 * <p>
	 * String-based search criteria are matched case-insensitively and
	 * diacritic-insensitively.
	 *
	 * @param searchCriteria criteria defining the cars to search for
	 * @return cars matching the specified search criteria
	 */
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

	/**
	 * Updates the editable properties of an existing car.<br>
	 * To get the up-to-date representation call {@link #getCar(Integer)}
	 *
	 * @param carId identifier of the car to update
	 * @param carToSave car containing the values to update
	 * @throws ResourceNotFound if no car with the specified identifier exists
	 */
	@Transactional
	public void updateCar(Integer carId, Car carToSave){
		var car = getCar(carId);

		car.setBrand(carToSave.getBrand());
		car.setName(carToSave.getName());
		car.setHorsePower(carToSave.getHorsePower());
		car.setTorque(carToSave.getTorque());
		car.setWheelDrive(carToSave.getWheelDrive());
		car.setFromDlc(carToSave.isFromDlc());

		repository.save(car);
	}

}
