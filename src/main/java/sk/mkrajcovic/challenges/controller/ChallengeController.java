package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;
import static sk.mkrajcovic.challenges.security.UserRoles.PARTICIPANT;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.*;
import sk.mkrajcovic.challenges.controller.mapper.ChallengeMapper;
import sk.mkrajcovic.challenges.controller.util.CreatedResponseEntity;
import sk.mkrajcovic.challenges.search.SearchChallengesCriteria;
import sk.mkrajcovic.challenges.service.ChallengeService;
import sk.mkrajcovic.challenges.service.ParticipantService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges")
public class ChallengeController {

	private final ChallengeService challengeService;
	private final ParticipantService participantService;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	List<ChallengeSummaryResponse> searchChallenges(@ModelAttribute SearchChallengesCriteria searchCriteria) {
		return challengeService.searchChallenges(searchCriteria).stream()
			.map(ChallengeMapper::toSummaryResponse)
			.toList();
	}

	@GetMapping(path = "/{challengeId}", produces = APPLICATION_JSON_VALUE)
	ChallengeDetailResponse getChallenge(@PathVariable @Positive Integer challengeId) {
		var challenge = challengeService.getChallenge(challengeId);
		return ChallengeMapper.toDetailResponse(challenge);
	}

	@RolesAllowed(ADMIN)
	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	CreatedResponseEntity createChallenge(@Valid @RequestBody CreateChallengeRequest challenge) {
		Integer challengeId = challengeService.createChallenge(
				challenge.trackId(),
				challenge.carId(),
				challenge.endDate()
		);
		return CreatedResponseEntity.create("/challenges/{challengeId}", challengeId);
	}

	@RolesAllowed(PARTICIPANT)
	@PostMapping(path = "/{challengeId}/register")
	void registerForChallenge(@PathVariable @Positive Integer challengeId) {
		challengeService.registerForChallenge(challengeId);
	}

	@RolesAllowed({ADMIN, PARTICIPANT})
	@PutMapping(path = "/{challengeId}/participant", consumes = APPLICATION_JSON_VALUE)
	void updateLapTime(@PathVariable @Positive Integer challengeId, @Valid @RequestBody UpdateLapTimeRequest request) {
		participantService.updateLapTime(challengeId, request.participantName(), request.newLapTime());
	}

	@RolesAllowed(ADMIN)
	@PutMapping(path = "/{challengeId}", consumes = APPLICATION_JSON_VALUE)
	void updateChallengeEndDate(@PathVariable @Positive Integer challengeId, @Valid @RequestBody UpdateChallengeEndDateRequest request) {
		challengeService.updateChallengeEndDate(challengeId, request.endDate());
	}

	@RolesAllowed(ADMIN)
	@DeleteMapping(path = "/{challengeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteChallenge(@PathVariable @Positive Integer challengeId) {
		challengeService.deleteChallenge(challengeId);
	}
}
