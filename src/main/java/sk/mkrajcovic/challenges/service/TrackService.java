package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.TrackDetail;
import sk.mkrajcovic.challenges.repository.persistence.TrackRepository;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;
import sk.mkrajcovic.challenges.search.SearchTracksCriteria;
import sk.mkrajcovic.challenges.util.Text;

@Service
@RequiredArgsConstructor
public class TrackService {

	private final TrackRepository repository;

	@Transactional
	public Integer createTrack(Track track) {
		return repository.save(track).getId();
	}

	public Track getTrack(Integer trackId) {
		return EntityUtils.getExistingEntityById(repository, trackId);
	}

	public List<TrackDetail> searchTracks(SearchTracksCriteria searchCriteria) {
		normalizeSearchCriteria(searchCriteria);
		return repository.findTracks(searchCriteria);
	}

	/*
	 * Mutation is intentional because the criteria object is passed to the
	 * repository afterwards and have no other usage really.
	 */
	private void normalizeSearchCriteria(SearchTracksCriteria criteria) {
		criteria.setCountry(Text.normalizeForSearch(criteria.getCountry()));
		criteria.setName(Text.normalizeForSearch(criteria.getName()));
	}
}
