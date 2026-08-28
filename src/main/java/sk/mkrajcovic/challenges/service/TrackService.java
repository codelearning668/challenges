package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.repository.TrackRepository;
import sk.mkrajcovic.challenges.repository.TrackRepository.TrackData;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;

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

	public List<TrackData> searchTracks() {
		return repository.findTracks();
	}

}
