package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.api.TrackApi;
import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.controller.dto.TrackDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.UpdateTrackRequest;
import sk.mkrajcovic.challenges.controller.mapper.TrackMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchTracksCriteria;
import sk.mkrajcovic.challenges.service.TrackService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tracks")
class TrackController implements TrackApi {

	private final TrackService service;

	@GetMapping(path = "/{trackId}", produces = APPLICATION_JSON_VALUE)
	public TrackDetailResponse getTrack(@PathVariable @Positive Integer trackId) {
		var track = service.getTrack(trackId);
		return TrackMapper.toDetailResponse(track);
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public List<TrackDetailResponse> searchTracks(@ModelAttribute SearchTracksCriteria searchCriteria) {
		return service.searchTracks(searchCriteria).stream()
			.map(TrackMapper::toDetailResponse)
			.toList();
	}

	@RolesAllowed(ADMIN)
	@PostMapping(produces = APPLICATION_JSON_VALUE)
	public CreatedResponseEntity createTrack(@Valid @RequestBody CreateTrackRequest request) {
		Integer trackId = service.createTrack(TrackMapper.toTrack(request), request.simulatorId());
		return CreatedResponseEntity.create("/tracks/{trackId}", trackId);
	}

	@RolesAllowed(ADMIN)
	@PutMapping(path = "/{trackId}", consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateTrack(@PathVariable @Positive Integer trackId, @Valid @RequestBody UpdateTrackRequest request){
		service.updateTrack(trackId, TrackMapper.toTrack(request));
	}

}
