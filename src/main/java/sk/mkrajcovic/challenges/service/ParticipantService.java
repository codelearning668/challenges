package sk.mkrajcovic.challenges.service;

import static java.util.Objects.requireNonNull;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.exception.AccessDenied;
import sk.mkrajcovic.challenges.exception.BusinessViolation;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.repository.persistence.ParticipantRepository;

@Service
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantRepository repository;
	private CallContext callContext;

	@Autowired
	void setCallContext(CallContext callContext) {
		this.callContext = callContext;
	}

	/**
	 * Registers a participant for the specified challenge.
	 * <p>
	 * The participant is identified by name and initially has no recorded lap time.
	 *
	 * @param name the name of the participant to register
	 * @param challenge the challenge for which the participant is registered
	 * @throws NullPointerException if {@code name} or {@code challenge} is {@code null}
	 */
	@Transactional
	public void registerParticipant(String name, Challenge challenge) {
		var participant = new Participant();
		participant.setName(requireNonNull(name, "cannot create participant without a name"));
		participant.setChallenge(requireNonNull(challenge, "cannot register for non-existent challenge"));

		repository.save(participant);
	}

	/**
	 * Updates the participant's lap time and updates the challenge's current leader
	 * when the new time changes the best result.
	 * <p>
	 * Non-administrative users may update only their own lap time and only while
	 * the challenge is active. Administrators may update any registered
	 * participant's lap time, including after the challenge has ended.
	 *
	 * @param challengeId the identifier of the challenge
	 * @param participantName the name of the registered participant whose lap time is being updated
	 * @param newLapTime the new lap time recorded for the participant; may be {@code null} when removing the time
	 * @throws ResourceNotFound if the participant is not registered for the challenge
	 * @throws BusinessViolation if a non-administrator attempts to update a lap time after the challenge has ended
	 * @throws AccessDenied if a non-administrator attempts to update another participant's lap time
	 */
	@Transactional
	public void updateLapTime(Integer challengeId, String participantName, Duration newLapTime) {
		var participant = lookupRegisteredParticipant(challengeId, participantName);

		// because only ADMIN can change someone else's lap time even after challenge has ended
		if (!callContext.isUserInRole(ADMIN)) {
			verifyChallengeIsActive(participant.getChallenge());
			verifyParticipantIsCurrentUser(participantName);
		}

		participant.setBestLapTime(newLapTime);
		updateChallengeLeader(participant.getChallenge(), participant);

		repository.save(participant);
	}

	private void updateChallengeLeader(Challenge challenge, Participant participant) {
		if (participantWasLeader(participant, challenge) && participantNoLongerHasTheBestTime(challenge, participant)) {
			recomputeChallengeLeader(challenge);

		} else if (participantHasFasterTimeThanChallengeLeader(participant, challenge)) {
			setChallengeLeader(challenge, participant);
		}
	}

	private boolean participantWasLeader(Participant participant, Challenge challenge) {
		return participant.getName().equals(challenge.getBestParticipantName());
	}

	private boolean participantNoLongerHasTheBestTime(Challenge challenge, Participant participant) {
		var participantLapTime = participant.getBestLapTime();
		var challengeBestLapTime = challenge.getBestLapTime();

		return participantLapTime == null
			|| challengeBestLapTime == null
			|| participantLapTime.compareTo(challengeBestLapTime) > 0;
	}

	private void recomputeChallengeLeader(Challenge challenge) {
		// TODO: update the comparator to resolve ties
		var quickestParticipant = challenge.getParticipants().stream()
			.filter(participant -> participant.getBestLapTime() != null)
			.min(Comparator.comparing(Participant::getBestLapTime));

		quickestParticipant.ifPresentOrElse(
			quickest -> setChallengeLeader(challenge, quickest),
			() -> clearChallengeLeader(challenge));
	}

	private boolean participantHasFasterTimeThanChallengeLeader(Participant participant, Challenge challenge) {
		return participant.getBestLapTime() != null
			&& (challenge.getBestLapTime() == null
				|| participant.getBestLapTime().compareTo(challenge.getBestLapTime()) < 0);
	}

	private void setChallengeLeader(Challenge challenge, Participant participant) {
		challenge.setBestParticipantName(participant.getName());
		challenge.setBestLapTime(participant.getBestLapTime());
	}

	private void clearChallengeLeader(Challenge challenge) {
		challenge.setBestParticipantName(null);
		challenge.setBestLapTime(null);
	}

	private Participant lookupRegisteredParticipant(Integer challengeId, String participantName) {
		return repository.findByChallengeIdAndName(challengeId, participantName)
			.orElseThrow(() -> new ResourceNotFound(PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE));
	}

	// TODO: code duplication, move this method from this and challengeService
	// in different class like ChallengeValidator..
	private void verifyChallengeIsActive(Challenge challenge) {
		var today = LocalDate.now();
		if (challenge.getEndDate().isBefore(today)) {
			throw new BusinessViolation(CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE);
		}
	}

	private void verifyParticipantIsCurrentUser(String participantName) {
		if (!callContext.getCurrentUser().equals(participantName)) {
			throw new AccessDenied(CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME);
		}
	}

}
