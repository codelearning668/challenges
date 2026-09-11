package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
import sk.mkrajcovic.challenges.controller.mapper.TrackMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchTracksCriteria;
import sk.mkrajcovic.challenges.service.TrackService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
public class TrackController {

	private final TrackService service;

	@GetMapping(path = "/{trackId}", produces = APPLICATION_JSON_VALUE)
	TrackDetailResponse getTrack(@PathVariable @Positive Integer trackId) {
		var track = service.getTrack(trackId);
		return TrackMapper.toDetailResponse(track);
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	List<TrackDetailResponse> searchTracks(@ModelAttribute SearchTracksCriteria searchCriteria) {
		return service.searchTracks(searchCriteria).stream()
			.map(TrackMapper::toDetailResponse)
			.toList();
	}

	@RolesAllowed(ADMIN)
	@PostMapping(produces = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createTrack(@Valid @RequestBody CreateTrackRequest request) {
		Integer trackId = service.createTrack(TrackMapper.toTrack(request));
		return CreatedResponseEntity.create("/tracks/{trackId}", trackId);
	}

	@RolesAllowed(ADMIN)
	@PutMapping(path = "/{trackId}", consumes = APPLICATION_JSON_VALUE)
	Integer updateTrack(@PathVariable @Positive Integer trackId, @Valid @RequestBody CreateTrackRequest request){
		return service.updateTrack(trackId, TrackMapper.toTrack(request));
	}

	@RolesAllowed(ADMIN)
	@DeleteMapping(path = "/{trackId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteTrack(@PathVariable @Positive Integer trackId){
		service.deleteTrack(trackId);
	}
}
