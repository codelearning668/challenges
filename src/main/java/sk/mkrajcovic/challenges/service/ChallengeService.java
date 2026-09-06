package sk.mkrajcovic.challenges.service;

import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_REGISTER_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CHALLENGE_ALREADY_ACTIVE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.MULTI_CHALLENGE_REGISTRATION_REQUIRES_PREVIOUS_WIN;
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
import sk.mkrajcovic.challenges.exception.ResourceNotFound;
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

	/**
	 * Searches for challenges matching the specified search criteria.
	 * <p>
	 * A {@code null} search criteria is treated as empty criteria, resulting in an
	 * unfiltered search. Search criteria are normalized before being passed to the
	 * repository to ensure consistent matching of searchable text fields.
	 *
	 * @param searchCriteria criteria used to filter the challenges, may be {@code null}
	 * @return challenges matching the specified criteria, never {@code null}
	 */
	public List<ChallengeDetail> searchChallenges(SearchChallengesCriteria searchCriteria) {
		var criteria = searchCriteria != null ? searchCriteria : new SearchChallengesCriteria();
		normalizeSearchCriteria(criteria);
		return repository.findChallenges(criteria);
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

	/**
	 * Creates a new challenge for the specified track and car.<br>
	 * A new challenge can only be created if there is no currently active challenge
	 * for the same track and car.
	 * 
	 * @return the ID of the newly created challenge
	 * @throws BusinessViolation if an active challenge already exists for the
	 *                           specified track and car
	 */
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

	/**
	 * Retrieves the challenge with the specified ID.
	 *
	 * @param challengeId ID of the challenge to retrieve
	 * @return the challenge with the specified ID
	 * @throws ResourceNotFound if no challenge exists with the specified ID
	 */
	public Challenge getChallenge(Integer challengeId) {
		return EntityUtils.getExistingEntityById(repository, challengeId);
	}

	/**
	 * Registers the current participant for the specified challenge.<br>
	 * Registration is only allowed for active challenges and if the participant is
	 * not already registered.<br>
	 * If the participant is already competing in another active challenge, they
	 * must have previously won a challenge to be allowed to compete in multiple
	 * challenges simultaneously.
	 *
	 * @throws Conflict if the participant is already registered for the challenge
	 * @throws BusinessViolation if the challenge is closed or the participant is not
	 *                           eligible to compete in multiple active challenges
	 */
	@Transactional
	public void registerForChallenge(Integer challengeId) {
		var challenge = EntityUtils.getExistingEntityById(repository, challengeId);
		String participantName = callContext.getCurrentUser();

		verifyChallengeIsActive(challenge);
		verifyNotAlreadyRegistered(participantName, challenge);
		verifyCanRegisterForMultipleChallenges(participantName);

		participantService.registerParticipant(participantName, challenge);
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

	private void verifyCanRegisterForMultipleChallenges(String participantName) {
		if (repository.hasActiveChallengeWithoutPreviousWin(participantName)) {
			throw new BusinessViolation(MULTI_CHALLENGE_REGISTRATION_REQUIRES_PREVIOUS_WIN);
		}
	}
}
