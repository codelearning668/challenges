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

	public List<Simulator> getSimulators() {
		return repository.findAllByOrderByIdAsc();
	}

	public Simulator getSimulator(Integer simulatorId) {
		return EntityUtils.getExistingEntityById(repository, simulatorId);
	}
}
