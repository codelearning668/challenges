package sk.mkrajcovic.challenges.service;

import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_REGISTER_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CHALLENGE_ALREADY_ACTIVE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_ALREADY_REGISTERED_FOR_CHALLENGE;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.exception.BusinessViolation;
import sk.mkrajcovic.challenges.exception.Conflict;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.read.ChallengeDetail;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;
import sk.mkrajcovic.challenges.search.SearchChallengesCriteria;
import sk.mkrajcovic.challenges.util.Text;

@Service
@RequiredArgsConstructor
public class ChallengeService {

	private final ChallengeRepository repository;

	private final TrackService trackService;
	private final CarService carService;
	private final ParticipantService participantService;

	private CallContext callContext;

	// because it's a request scoped bean
	@Autowired
	void setCallContext(CallContext callContext) {
		this.callContext = callContext;
	}

	public List<ChallengeDetail> searchChallenges(SearchChallengesCriteria searchCriteria) {
		normalizeSearchCriteria(searchCriteria);
		return repository.findChallenges(searchCriteria);
	}

	/*
	 * Mutation is intentional because the criteria object is passed to the
	 * repository afterwards and have no other usage really.
	 */
	private void normalizeSearchCriteria(SearchChallengesCriteria criteria) {
		criteria.setCarBrand(Text.normalizeForSearch(criteria.getCarBrand()));
		criteria.setCarName(Text.normalizeForSearch(criteria.getCarName()));
		criteria.setTrackCountry(Text.normalizeForSearch(criteria.getTrackCountry()));
		criteria.setTrackName(Text.normalizeForSearch(criteria.getTrackName()));
		criteria.setBestParticipantName(Text.normalizeForSearch(criteria.getBestParticipantName()));
	}

	@Transactional
	public Integer createChallenge(Integer trackId, Integer carId, LocalDate endDate) {
		verifyChallengeNotActive(trackId, carId);

		var track = trackService.getTrack(trackId);
		var car = carService.getCar(carId);

		var challenge = new Challenge();
		challenge.setTrack(track);
		challenge.setCar(car);
		challenge.setEndDate(endDate);

		return repository.save(challenge).getId();
	}

	private void verifyChallengeNotActive(Integer trackId, Integer carId) {
		if (repository.existsActiveChallengeForTrackAndCar(trackId, carId)) {
			throw new Conflict(CHALLENGE_ALREADY_ACTIVE);
		}
	}

	public Challenge getChallenge(Integer challengeId) {
		return EntityUtils.getExistingEntityById(repository, challengeId);
	}

	@Transactional
	public void registerForChallenge(Integer challengeId) {
		var challenge = EntityUtils.getExistingEntityById(repository, challengeId);
		String participant = getCurrentUserName();

		verifyChallengeIsActive(challenge);
		// when adding logic around registering to multiple challenges
		// this check will need to be broader as it needs to verify the participant
		// is not currently participating in any other active challenges
		verifyNotAlreadyRegistered(participant, challenge);

		participantService.registerParticipant(participant, challenge);
	}

	private String getCurrentUserName() {
		String username = callContext.getCurrentUser();
		assert username != null : "user calling this should always be present in the security context";
		return username;
	}

	private void verifyChallengeIsActive(Challenge challenge) {
		var today = LocalDate.now();
		var endDate = challenge.getEndDate();

		if (endDate.isBefore(today)) {
			throw new BusinessViolation(CANNOT_REGISTER_ON_CLOSED_CHALLENGE, endDate);
		}
	}

	private void verifyNotAlreadyRegistered(String newParticipantName, Challenge challenge) {
		boolean alreadyAssigned = challenge.getParticipants().stream()
				.anyMatch(participant -> participant.getName().equals(newParticipantName));

		if (alreadyAssigned) {
			throw new Conflict(PARTICIPANT_ALREADY_REGISTERED_FOR_CHALLENGE);
		}
	}
}
