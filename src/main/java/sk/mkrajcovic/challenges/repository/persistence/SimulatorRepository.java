package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Simulator;

@Repository
public interface SimulatorRepository extends JpaRepository<Simulator, Integer> {

	List<Simulator> findAllByOrderByIdAsc();
}
