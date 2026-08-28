package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {

	@Query("""
		SELECT t.id as id, t.country as country, t.name as name, t.lengthKm as lengthKm FROM Track t
	""")
	public List<TrackData> findTracks();

	interface TrackData {
		Integer getId();
		String getCountry();
		String getName();
		Double getLengthKm();
	}

}
