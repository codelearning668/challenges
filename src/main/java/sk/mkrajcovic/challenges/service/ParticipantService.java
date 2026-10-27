package sk.mkrajcovic.challenges.service;

import static java.util.Objects.requireNonNull;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.NEW_LAP_TIME_MUST_BE_FASTER_THAN_PREVIOUS;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_LAP_TIME_MUST_BE_DEFINED;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOG = LoggerFactory.getLogger(ParticipantService.class);

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
	 * <p>
	 * The caller is responsible for verifying that the participant is not already
	 * registered for the challenge and that registration is otherwise permitted.
	 *
	 * @param name the name of the participant to register
	 * @param challenge the challenge for which the participant is registered
	 * @throws NullPointerException if {@code name} or {@code challenge} is {@code null}
	 */
	@Transactional
	public void registerParticipant(String name, Challenge challenge) {
		requireNonNull(name, "cannot create participant without a name");
		requireNonNull(challenge, "cannot register for non-existent challenge");

		var participant = repository.findByChallengeIdAndName(challenge.getId(), name).orElseGet(Participant::new);
		participant.setName(name);
		participant.setChallenge(challenge);

		repository.save(participant);
	}

	/**
	 * Removes the participant's registration from the specified challenge and
	 * recomputes its leader from the remaining participants.
	 * <p>
	 * The caller is responsible for verifying that leaving the challenge is
	 * currently permitted.
	 *
	 * @param participantName the participant's name
	 * @param challenge the challenge from which the participant is removed
	 * @throws NullPointerException if {@code name} or {@code challenge} is {@code null}
	 */
	@Transactional
	public void unregisterParticipant(String participantName, Challenge challenge) {
		requireNonNull(participantName, "cannot unregister participant without a name");
		requireNonNull(challenge, "cannot unregister from non-existent challenge");

		repository.deleteByChallengeIdAndName(challenge.getId(), participantName);
		/*
		 * Removing the participant from the collection ensures that leader
		 * re-computation does not select the participant who has just quit.
		 */
		synchronizeChallengeParticipantsForRemoval(challenge, participantName);

		recomputeChallengeLeader(challenge);
	}

	/**
	 * This updates only the inverse in-memory collection; it does not persist
	 * removal of the participant relationship. The repository delete does that.
	 */
	private void synchronizeChallengeParticipantsForRemoval(Challenge challenge, String participantName) {
		challenge.getParticipants()
			.removeIf(participant -> participantName.equals(participant.getName())
		);
	}

	/**
	 * Updates a registered participant's lap time and recalculates the challenge's
	 * current leader when the change affects the best result.
	 * <p>
	 * Participants may update only their own lap time while the challenge is
	 * active. They must provide a lap time, and when one is already recorded, the
	 * new time must be strictly faster. If no time is recorded yet, participant may
	 * set their first time.
	 * <p>
	 * Administrators may update any registered participant's time, including after
	 * the challenge has ended. They may clear a time by passing {@code null} or
	 * replace it with a slower time.
	 *
	 * @param challengeId the identifier of the challenge
	 * @param targetParticipantName the name of the registered participant whose lap time is being updated
	 * @param newLapTime the new lap time; {@code null} clears the time for administrators and is not permitted for participants
	 * @throws ResourceNotFound if the participant is not registered for the challenge
	 * @throws BusinessViolation if a participant updates a time after the challenge has ended, supplies no time, or does not improve
	 * @throws AccessDenied if a participant attempts to update another participant's lap time
	 */
	@Transactional
	public void updateLapTime(Integer challengeId, String targetParticipantName, Duration newLapTime) {
		var participant = lookupRegisteredParticipant(challengeId, targetParticipantName);
		verifyLapTimeUpdateAllowed(participant, targetParticipantName, newLapTime);

		participant.setBestLapTime(newLapTime);
		updateChallengeLeader(participant.getChallenge(), participant);

		repository.save(participant);
	}

	private Participant lookupRegisteredParticipant(Integer challengeId, String participantName) {
		return repository.findByChallengeIdAndName(challengeId, participantName)
			.orElseThrow(() -> new ResourceNotFound(PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE));
	}

	private void verifyLapTimeUpdateAllowed(Participant participant, String targetParticipantName, Duration newLapTime) {
		/*
		 * ADMIN may override the normal lap-time rules to keep results fair:
		 * - remove a time when participants agree it violated the racing rules
		 * - apply a slower time as a penalty while keeping the participant's result
		 * - adjust a result when changed conditions make the previous performance unattainable
		 *
		 * Every such override is recorded for the audit trail.
		 */
		if (callContext.isUserInRole(ADMIN)) {
			logAdminOverride(participant, newLapTime);
			return;
		}
		verifyChallengeIsActive(participant.getChallenge());
		verifyParticipantIsCurrentUser(targetParticipantName);
		verifyNewLapTimeIsDefined(newLapTime);
		verifyNewLapTimeIsBetterThanPrevious(newLapTime, participant);
	}

	private void logAdminOverride(Participant participant, Duration newLapTime) {
		// reads must run before the override is applied: this is the pre-override time
		Duration previousLapTime = participant.getBestLapTime();
		LOG.info("Admin '{}' overrides lap time in challenge {}: participant '{}', {} -> '{}'",
			callContext.getCurrentUser(),
			participant.getChallenge().getId(),
			participant.getName(),
			previousLapTime,
			newLapTime
		);
	}

	private void verifyChallengeIsActive(Challenge challenge) {
		var today = LocalDate.now(ZoneOffset.UTC);
		if (challenge.getEndDate().isBefore(today)) {
			throw new BusinessViolation(CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE);
		}
	}

	private void verifyParticipantIsCurrentUser(String participantName) {
		if (!callContext.getCurrentUser().equals(participantName)) {
			throw new AccessDenied(CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME);
		}
	}

	private void verifyNewLapTimeIsDefined(Duration newLapTime) {
		if (newLapTime == null) {
			throw new BusinessViolation(PARTICIPANT_LAP_TIME_MUST_BE_DEFINED);
		}
	}

	private void verifyNewLapTimeIsBetterThanPrevious(Duration newLapTime, Participant participant) {
		if (participant.getBestLapTime() != null && newLapTime.compareTo(participant.getBestLapTime()) >= 0) {
			throw new BusinessViolation(NEW_LAP_TIME_MUST_BE_FASTER_THAN_PREVIOUS);
		}
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
		var previousLeader = challenge.getBestParticipantName();
		challenge.setBestParticipantName(participant.getName());
		challenge.setBestLapTime(participant.getBestLapTime());

		logLeaderChange(challenge.getId(), previousLeader, participant);
	}

	private void logLeaderChange(Integer challengeId, String previousLeaderName, Participant newLeader) {
		var newLeaderName = newLeader.getName();
		if (!newLeaderName.equals(previousLeaderName)) {
			LOG.debug("Leader of challenge {}: changed '{}' -> '{}'",
				challengeId,
				previousLeaderName,
				newLeaderName
			);
		}
	}

	private void clearChallengeLeader(Challenge challenge) {
		challenge.setBestParticipantName(null);
		challenge.setBestLapTime(null);

		LOG.debug("Leader of challenge {} cleared; no timed participants left",
			challenge.getId()
		);
	}

}
