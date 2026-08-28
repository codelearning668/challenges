package sk.mkrajcovic.challenges.repository.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Participant;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

	public Optional<Participant> findByChallengeIdAndName(Integer challengeId, String participantName);

}
