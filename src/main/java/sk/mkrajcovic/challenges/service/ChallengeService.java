package sk.mkrajcovic.challenges.service;

import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_DELETE_ALREADY_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_QUIT_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_REGISTER_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_END_DATE_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CAR_AND_TRACK_MUST_USE_SAME_SIMULATOR;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CHALLENGE_ALREADY_ACTIVE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.MULTI_CHALLENGE_REGISTRATION_REQUIRES_PREVIOUS_WIN;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_ALREADY_REGISTERED_FOR_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE;
import static sk.mkrajcovic.challenges.repository.util.EntityUtils.getExistingEntityById;

import java.time.LocalDate;
import java.time.ZoneOffset;
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
import sk.mkrajcovic.challenges.model.Car;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.ChallengeDetail;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
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
	 * Creates a new challenge for the specified track and car.
	 * <p>
	 * A new challenge can only be created if there is no currently active challenge
	 * for the same track and car.
	 *
	 * @return the ID of the newly created challenge
	 * @throws BusinessViolation if the selected car and track use different simulators
	 * @throws Conflict if an active challenge already exists for the selected track and car
	 */
	@Transactional
	public Integer createChallenge(Integer trackId, Integer carId, LocalDate endDate) {
		verifyChallengeNotActive(trackId, carId);

		var track = trackService.getTrack(trackId);
		var car = carService.getCar(carId);

		verifyTrackAndCarUseSameSimulator(track, car);

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

	private void verifyTrackAndCarUseSameSimulator(Track track, Car car) {
		if (!track.getSimulator().getId().equals(car.getSimulator().getId())) {
			throw new BusinessViolation(CAR_AND_TRACK_MUST_USE_SAME_SIMULATOR);
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
		return getExistingEntityById(repository, challengeId);
	}

	/**
	 * Changes the end date of a challenge that is currently active.
	 * Closed challenges cannot have their end date modified.
	 *
	 * @param challengeId ID of the challenge whose end date is being changed
	 * @param endDate new end date for the challenge
	 * @throws ResourceNotFound if no challenge exists with the specified ID
	 * @throws BusinessViolation if the challenge is already closed
	 */
	@Transactional
	public void updateChallengeEndDate(Integer challengeId, LocalDate endDate) {
		var challenge = getExistingEntityById(repository, challengeId);

		verifyChallengeIsActive(challenge, CANNOT_UPDATE_END_DATE_ON_CLOSED_CHALLENGE);

		challenge.setEndDate(endDate);

		repository.save(challenge);
	}

	/**
	 * Deletes a challenge that is currently active.
	 * Once a challenge has been closed, it cannot be deleted.
	 *
	 * @param challengeId ID of the challenge to delete
	 * @throws ResourceNotFound if no challenge exists with the specified ID
	 * @throws BusinessViolation if the challenge is already closed
	 */
	public void deleteChallenge(Integer challengeId){
		var challenge = getExistingEntityById(repository, challengeId);

		verifyChallengeIsActive(challenge, CANNOT_DELETE_ALREADY_CLOSED_CHALLENGE);

		repository.delete(challenge);
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
		var challenge = getExistingEntityById(repository, challengeId);
		String participantName = callContext.getCurrentUser();

		verifyChallengeIsActive(challenge, CANNOT_REGISTER_ON_CLOSED_CHALLENGE);
		verifyNotAlreadyRegistered(participantName, challenge);
		verifyCanRegisterForMultipleChallenges(participantName);

		participantService.registerParticipant(participantName, challenge);
	}

	private void verifyChallengeIsActive(Challenge challenge, String messageCode) {
		var today = LocalDate.now(ZoneOffset.UTC);
		var endDate = challenge.getEndDate();

		if (endDate.isBefore(today)) {
			throw new BusinessViolation(messageCode, endDate);
		}
	}

	private void verifyNotAlreadyRegistered(String newParticipantName, Challenge challenge) {
		if (isRegistered(newParticipantName, challenge)) {
			throw new Conflict(PARTICIPANT_ALREADY_REGISTERED_FOR_CHALLENGE);
		}
	}

	private boolean isRegistered(String participantName, Challenge challenge) {
		return challenge.getParticipants().stream()
				.anyMatch(participant -> participant.getName().equals(participantName));
	}

	private void verifyCanRegisterForMultipleChallenges(String participantName) {
		if (repository.hasActiveChallengeWithoutPreviousWin(participantName)) {
			throw new BusinessViolation(MULTI_CHALLENGE_REGISTRATION_REQUIRES_PREVIOUS_WIN);
		}
	}

	/**
	 * Removes the current participant from the specified active challenge.
	 * <p>
	 * A participant may quit only while the challenge is active and only when they
	 * are currently registered for it.
	 *
	 * @param challengeId ID of the challenge to quit
	 * @throws ResourceNotFound if no challenge exists with the specified ID
	 * @throws Conflict if the participant is not registered for the challenge
	 * @throws BusinessViolation if the challenge is already closed
	 */
	public void quitChallenge(Integer challengeId) {
		var challenge = getExistingEntityById(repository, challengeId);
		var participantName = callContext.getCurrentUser();

		verifyChallengeIsActive(challenge, CANNOT_QUIT_CLOSED_CHALLENGE);
		verifyAlreadyRegistered(participantName, challenge);

		participantService.unregisterParticipant(participantName, challenge);
	}

	private void verifyAlreadyRegistered(String participantName, Challenge challenge) {
		if (!isRegistered(participantName, challenge)) {
			throw new Conflict(PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE);
		}
	}

}
