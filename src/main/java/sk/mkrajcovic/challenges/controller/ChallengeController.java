package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;
import static sk.mkrajcovic.challenges.security.UserRoles.PARTICIPANT;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.ChallengeDetailResponse;
import sk.mkrajcovic.challenges.controller.dto.CreateChallengeRequest;
import sk.mkrajcovic.challenges.controller.dto.UpdateLapTimeRequest;
import sk.mkrajcovic.challenges.controller.mapper.ChallengeMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository.ChallengeData;
import sk.mkrajcovic.challenges.search.SearchChallengesCriteria;
import sk.mkrajcovic.challenges.service.ChallengeService;
import sk.mkrajcovic.challenges.service.ParticipantService;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

	private final ChallengeService challengeService;
	private final ParticipantService participantService;

	// TODO mkrajcovic: add pagination LATER
	// TODO: review - return challengeSearchResponse dto? has this some real boundary or maintainability issue?
	// can we assume the projection as domain object? (btw, entities are not domain objects, so we are down a layer anyway)
	@GetMapping(path = "/challenges/", produces = APPLICATION_JSON_VALUE)
	List<ChallengeData> searchChallanges(@ModelAttribute SearchChallengesCriteria searchCriteria) {
		return challengeService.searchChallenges(searchCriteria);
	}

	// TODO: allow updating the challenge endDate for ADMIN!
	// +/or cancel challenge immediately:
	// -> time remaining in the day might be a problem, when validating based only by endDate

	@RolesAllowed(ADMIN)
	@PostMapping(path = "/challenge", consumes = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createChallenge(@Valid @RequestBody CreateChallengeRequest challenge) {
		Integer challengeId = challengeService.createChallenge(
				challenge.trackId(),
				challenge.carId(),
				challenge.endDate()
		);
		return CreatedResponseEntity.create("/challenge/{challengeId}", challengeId);
	}

	@GetMapping(path = "/challenge/{challengeId}", produces = APPLICATION_JSON_VALUE)
	ChallengeDetailResponse getChallenge(@PathVariable @Positive Integer challengeId) {
		var challenge = challengeService.getChallenge(challengeId);
		return ChallengeMapper.toDetailResponse(challenge);
	}

	@RolesAllowed(PARTICIPANT)
	@PostMapping(path = "/challenge/{challengeId}/register")
	void registerForChallenge(@PathVariable @Positive Integer challengeId) {
		challengeService.registerForChallenge(challengeId);
	}

	@RolesAllowed({ADMIN, PARTICIPANT})
	@PutMapping(path = "/challenge/{challengeId}/participant", consumes = APPLICATION_JSON_VALUE)
	void updateLapTime(@PathVariable @Positive Integer challengeId, @Valid @RequestBody UpdateLapTimeRequest request) {
		participantService.updateLapTime(challengeId, request.participantName(), request.newLapTime());
	}

}
