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

}
