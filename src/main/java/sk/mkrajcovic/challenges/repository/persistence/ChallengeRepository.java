package sk.mkrajcovic.challenges.repository.persistence;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.controller.dto.SearchChallengesCriteria;
import sk.mkrajcovic.challenges.model.Challenge;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

	@Query("""
		SELECT ch.id as id,
		       ch.endDate as endDate,
		       tr.name as trackName,
		       tr.country as trackCountry,
		       c.brand as carBrand,
		       c.name as carName
		FROM Challenge ch
		JOIN ch.track tr
		JOIN ch.car c
		WHERE (cast(:#{#criteria.endDate} as text) IS NULL OR ch.endDate = :#{#criteria.endDate})
		AND (:#{#criteria.trackName} IS NULL OR tr.name LIKE %:#{#criteria.trackName}%)
		AND (:#{#criteria.trackCountry} IS NULL OR tr.country LIKE %:#{#criteria.trackCountry}%)
		AND (:#{#criteria.carBrand} IS NULL OR c.brand LIKE %:#{#criteria.carBrand}%)
		AND (:#{#criteria.carName} IS NULL OR c.name LIKE %:#{#criteria.carName}%)
	""")
	public List<ChallengeData> searchChallenges(@Param("criteria") SearchChallengesCriteria criteria);

	interface ChallengeData {
		Integer getId();
		LocalDate getEndDate();
		String getTrackName();
		String getTrackCountry();
		String getCarBrand();
		String getCarName();
	}

}
