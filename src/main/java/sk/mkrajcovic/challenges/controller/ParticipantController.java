package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;
import static sk.mkrajcovic.challenges.security.UserRoles.PARTICIPANT;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.api.ParticipantApi;
import sk.mkrajcovic.challenges.controller.dto.ParticipantTimelineResponse;
import sk.mkrajcovic.challenges.controller.mapper.ParticipantTimelineMapper;
import sk.mkrajcovic.challenges.service.ParticipantTimelineService;

@RestController
@RequiredArgsConstructor
public class ParticipantController implements ParticipantApi {

	private final ParticipantTimelineService timelineService;

	@RolesAllowed({ADMIN, PARTICIPANT})
	@GetMapping(path = "/participant/{participantId}/timeline", produces = APPLICATION_JSON_VALUE)
	public List<ParticipantTimelineResponse> getParticipantTimeline(@PathVariable @Positive Integer participantId) {
		return timelineService.getTimeline(participantId).stream()
			.map(ParticipantTimelineMapper::toResponse)
			.toList();
	}
}
