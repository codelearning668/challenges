package sk.mkrajcovic.challenges.service;

import static java.util.Objects.requireNonNull;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.exception.ChallengeAlreadyEnded;
import sk.mkrajcovic.challenges.exception.InvalidParticipantAccess;
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

		verifyChallengeIsActive(participant.getChallenge());

		if (!callContext.isUserInRole(ADMIN)) {
			verifyParticipantIsCurrentUser(participantName);
		}

		participant.setBestLapTime(newLapTime);
		repository.save(participant);
	}

	private Participant lookupRegisteredParticipant(Integer challengeId, String participantName) {
		return repository.findByChallengeIdAndName(challengeId, participantName)
			.orElseThrow(() -> new ResourceNotFound("participant: " + participantName + " is not found for this challenge"));
	}

	// TODO: code duplication, move this method from this and challengeService
	// in different class like ChallengeValidator..
	private void verifyChallengeIsActive(Challenge challenge) {
		var today = LocalDate.now();
		if (challenge.getEndDate().isBefore(today)) {
			throw new ChallengeAlreadyEnded("this event has ended");
		}
	}

	private void verifyParticipantIsCurrentUser(String participantName) {
		if (!callContext.getCurrentUser().equals(participantName)) {
			throw new InvalidParticipantAccess("participant cannot update other participant lap time!");
		}
	}

}
