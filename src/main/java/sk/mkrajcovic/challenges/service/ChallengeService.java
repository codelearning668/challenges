package sk.mkrajcovic.challenges.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.exception.ChallengeAlreadyEnded;
import sk.mkrajcovic.challenges.exception.Conflict;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository.ChallengeData;
import sk.mkrajcovic.challenges.repository.util.EntityUtils;

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

	public List<ChallengeData> searchChallenges() {
		return repository.searchChallenges();
	}

	@Transactional
	public Integer createChallenge(Integer trackId, Integer carId, LocalDate endDate) {
		var track = trackService.getTrack(trackId);
		var car = carService.getCar(carId);

		var challenge = new Challenge();
		challenge.setTrack(track);
		challenge.setCar(car);
		challenge.setEndDate(endDate);

		return repository.save(challenge).getId();
	}

	public Challenge getChallenge(Integer challengeId) {
		return EntityUtils.getExistingEntityById(repository, challengeId);
	}

	@Transactional
	public void registerForChallenge(Integer challengeId) {
		var challenge = EntityUtils.getExistingEntityById(repository, challengeId);
		String participant = getCurrentUserName();

		verifyChallengeIsActive(challenge);
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
		if (challenge.getEndDate().isBefore(today)) {
			throw new ChallengeAlreadyEnded("this event has ended");
		}
	}

	private void verifyNotAlreadyRegistered(String newParticipantName, Challenge challenge) {
		boolean alreadyAssigned = challenge.getParticipants().stream()
				.anyMatch(participant -> participant.getName().equals(newParticipantName));

		if (alreadyAssigned) {
			throw new Conflict("you are already registered for this event");
		}
	}
}
