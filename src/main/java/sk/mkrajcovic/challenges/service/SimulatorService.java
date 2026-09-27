package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Simulator;
import sk.mkrajcovic.challenges.repository.persistence.SimulatorRepository;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;

@Service
@RequiredArgsConstructor
public class SimulatorService {

	private final SimulatorRepository repository;

	/**
	 * Returns all simulators ordered by their identifier in ascending order.
	 *
	 * @return simulators ordered by identifier
	 */
	public List<Simulator> getSimulators() {
		return repository.findAllByOrderByIdAsc();
	}

	/**
	 * @param simulatorId identifier of the simulator to retrieve
	 * @return the simulator with the specified identifier
	 * @throws ResourceNotFound if no simulator with the specified identifier exists
	 */
	public Simulator getSimulator(Integer simulatorId) {
		return EntityUtils.getExistingEntityById(repository, simulatorId);
	}
}
