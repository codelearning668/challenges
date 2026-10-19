package sk.mkrajcovic.challenges.service;

import static sk.mkrajcovic.challenges.enums.MessageCodeConstants.PARENT_ENTRY_NOT_IN_CHALLENGE;
import static sk.mkrajcovic.challenges.repository.util.EntityUtils.getExistingEntityById;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import sk.mkrajcovic.challenges.exception.BusinessViolation;
import sk.mkrajcovic.challenges.model.Challenge;
import sk.mkrajcovic.challenges.model.ConversationEntry;
import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.repository.persistence.ConversationEntryRepository;

@Service
@AllArgsConstructor
public class ConversationService {

	private final ConversationEntryRepository conversationRepository;

	/**
	 * Creates a conversation entry for a participant in the specified challenge,
	 * optionally assigning it a parent entry.
	 * <p>
	 * The participant's best lap time is stored as a snapshot at the time the entry
	 * is created. If a parent entry ID is supplied, that entry must belong to the
	 * same challenge.
	 *
	 * @param challenge the challenge to which the entry belongs
	 * @param participantName name of the participant posting the entry
	 * @param message content of the entry
	 * @param parentEntryId ID of the parent entry, or {@code null} for a top-level entry
	 * @throws BusinessViolation if the parent entry does not belong to the challenge
	 * @return id of the created entry
	 */
	@Transactional
	public Integer createConversationEntry(Challenge challenge, String participantName, String message, Integer parentEntryId) {
		var newEntry = new ConversationEntry();
		assignParentEntryIfValid(newEntry, parentEntryId, challenge);
		newEntry.setParticipantLapTimeAtPosting(findParticipantBestLapTime(challenge, participantName));
		newEntry.setParticipantName(participantName);
		newEntry.setContent(message);
		newEntry.setChallenge(challenge);

		return conversationRepository.save(newEntry).getId();
	}

	private void assignParentEntryIfValid(ConversationEntry newEntry, Integer parentEntryId, Challenge challenge) {
		if (parentEntryId != null) {
			verifyEntryBelongsToChallenge(parentEntryId, challenge.getId());
			newEntry.setParentEntryId(parentEntryId);
		}
	}

	private void verifyEntryBelongsToChallenge(Integer entryId, Integer challengeId) {
		if (!conversationRepository.existsByIdAndChallengeId(entryId, challengeId)) {
			throw new BusinessViolation(PARENT_ENTRY_NOT_IN_CHALLENGE);
		}
	}

	private Duration findParticipantBestLapTime(Challenge challenge, String participantName) {
		return challenge.getParticipants().stream()
			.filter(participant -> participant.getName().equals(participantName))
			.map(Participant::getBestLapTime)
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}

	/**
	 * Deletes a conversation entry while preserving its direct replies.
	 * <p>
	 * Replies to the deleted entry are retained as top-level entries so they do not
	 * reference an entry that no longer exists.
	 *
	 * @param entryId ID of the conversation entry to delete
	 * @throws ResourceNotFound if no conversation entry exists with the specified ID
	 */
	@Transactional
	public void deleteConversationEntry(Integer entryId) {
		var conversationEntry = getExistingEntityById(conversationRepository, entryId);
		clearParentReferencesToEntry(entryId);

		conversationRepository.delete(conversationEntry);
	}

	private void clearParentReferencesToEntry(Integer entryId) {
		// updates each reply individually so normal JPA entity behavior is
		// preserved and reply-specific handling can be added here if needed
		List<ConversationEntry> conversationEntries = conversationRepository.findAllByParentEntryId(entryId);
		conversationEntries.forEach(entry -> entry.setParentEntryId(null));

		conversationRepository.saveAllAndFlush(conversationEntries);
	}

}
