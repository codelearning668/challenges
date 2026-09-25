package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.TrackDetail;
import sk.mkrajcovic.challenges.search.SearchTracksCriteria;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {

	@Query("""
        SELECT t.id as id,
               t.country as country,
               t.name as name,
               t.lengthKm as lengthKm,
               s.type as simulatorType
        FROM Track t
        JOIN t.simulator s
        WHERE (:#{#criteria.country} IS NULL OR t.countrySearch LIKE %:#{#criteria.country}%)
        AND (:#{#criteria.name} IS NULL OR t.nameSearch LIKE %:#{#criteria.name}%)
        AND (:#{#criteria.lengthKm} IS NULL OR t.lengthKm = :#{#criteria.lengthKm})
        AND (:#{#criteria.simulatorId} IS NULL OR s.id = :#{#criteria.simulatorId})
    """)
	public List<TrackDetail> findTracks(@Param("criteria") SearchTracksCriteria criteria);

}
