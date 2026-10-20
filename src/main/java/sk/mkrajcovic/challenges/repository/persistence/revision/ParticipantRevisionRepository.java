package sk.mkrajcovic.challenges.repository.persistence.revision;

import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Participant;

@Repository
public interface ParticipantRevisionRepository extends RevisionRepository<Participant, Integer, Integer> {

}
