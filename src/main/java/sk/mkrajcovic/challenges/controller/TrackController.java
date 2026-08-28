package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.repository.TrackRepository.TrackData;
import sk.mkrajcovic.challenges.security.UserRoles;
import sk.mkrajcovic.challenges.service.TrackService;

@RestController
@RequiredArgsConstructor
public class TrackController {

	private final TrackService service;

	@RolesAllowed(UserRoles.ADMIN)
	@PostMapping(path = "/track", produces = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createTrack(@Valid @RequestBody CreateTrackRequest request) {
		Integer trackId = service.createTrack(toTrack(request));
		return CreatedResponseEntity.create("/track/{id}", trackId);
	}

	private Track toTrack(CreateTrackRequest request) {
		var track = new Track();
		track.setCountry(request.getCountry());
		track.setName(request.getName());
		track.setLengthKm(request.getLengthKm());
		return track;
	}

	@PermitAll
	@GetMapping(path = "/tracks/", produces = APPLICATION_JSON_VALUE)
	List<TrackData> searchTracks() {
		return service.searchTracks();
	}
}
