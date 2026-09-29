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
	private final SimulatorService simulatorService;

	/**
	 * Creates a new track and associates it with the specified simulator.
	 * 
	 * @param track track to create
	 * @param simulatorId identifier of the simulator associated with the track
	 * @return identifier of the newly created track
	 */
	@Transactional
	public Integer createTrack(Track track, Integer simulatorId) {
		track.setSimulator(simulatorService.getSimulator(simulatorId));
		return repository.save(track).getId();
	}

	/**
	 * Returns the track by the specified identifier.
	 *
	 * @param trackId identifier of the track to retrieve
	 * @return the track with the specified identifier
	 * @throws ResourceNotFound if no track with the specified identifier exists
	 */
	public Track getTrack(Integer trackId) {
		return EntityUtils.getExistingEntityById(repository, trackId);
	}

	/**
	 * Searches for tracks matching the specified search criteria.
	 * <p>
	 * String-based search criteria are matched case-insensitively and
	 * diacritic-insensitively.
	 *
	 * @param searchCriteria criteria defining the tracks to search for
	 * @return tracks matching the specified search criteria
	 */
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

	/**
	 * Updates the editable properties of an existing track.<br>
	 * To get the up-to-date representation call {@link #getTrack(Integer)}
	 *
	 * @param trackId identifier of the track to update
	 * @param trackToSave track containing the values to update
	 * @throws ResourceNotFound if no track with the specified identifier exists
	 */
	@Transactional
	public void updateTrack(Integer trackId, Track trackToSave){
		var track = getTrack(trackId);

		track.setCountry(trackToSave.getCountry());
		track.setName(trackToSave.getName());
		track.setLengthKm(trackToSave.getLengthKm());
		track.setFromDlc(trackToSave.isFromDlc());

		repository.save(track);
	}

}
