package sk.mkrajcovic.challenges.repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

	@Query("""
		SELECT ch.id as id,
		       ch.endDate as endDate,
		       tr.name as trackName,
		       tr.country as trackLocation,
		       c.brand as carBrand,
		       c.name as carName
		FROM Challenge ch
		JOIN ch.track tr
		JOIN ch.car c
	""")
	public List<ChallengeData> searchChallenges();

	// TODO: to be able to return participant data along with challenges:
	// i cannot fetch the hierarchical data with single query, so my service needs to combine this
	// in two steps..
	// Other option is to create a separate projection (java records) then group participant by challenge id
//	@Query("""
//		    SELECT p.challenge.id as challengeId,
//		           p.name as name,
//		           p.bestLapTime as lapTime
//		    FROM Participant p
//		    WHERE p.challenge.id IN :challengeIds
//		    """)
//		List<ParticipantData> findParticipants(
//		        @Param("challengeIds") Collection<Integer> challengeIds);
	
	interface ChallengeData {
		Integer getId();
		LocalDate getEndDate();
		String getTrackName();
		String getTrackLocation();
		String getCarBrand();
		String getCarName();
		Set<ParticipantData> getParticipants(); 
	}

	interface ParticipantData {
		String getName();
		Duration getLapTime(); 
	}
}
