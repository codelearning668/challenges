package sk.mkrajcovic.challenges.service;

import static java.util.Objects.requireNonNull;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME;
import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.time.Duration;
import java.time.LocalDate;

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

	@Transactional
	public void registerParticipant(String name, Challenge challenge) {
		var participant = new Participant();
		participant.setName(requireNonNull(name, "cannot create participant without a name"));
		participant.setChallenge(requireNonNull(challenge, "cannot register for non-existent challenge"));

		repository.save(participant);
	}

	@Transactional
	public void updateLapTime(Integer challengeId, String participantName, Duration newLapTime) {
		var participant = lookupRegisteredParticipant(challengeId, participantName);

		// because only ADMIN can change someone else's lap time even after challenge has ended
		if (!callContext.isUserInRole(ADMIN)) {
			verifyChallengeIsActive(participant.getChallenge());
			verifyParticipantIsCurrentUser(participantName);
		}

		participant.setBestLapTime(newLapTime);
		repository.save(participant);
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
