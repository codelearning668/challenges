package sk.mkrajcovic.challenges.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata.RevisionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.model.read.ParticipantTimelineEntry;
import sk.mkrajcovic.challenges.repository.persistence.revision.ParticipantRevisionRepository;

@Service
@RequiredArgsConstructor
public class ParticipantTimelineService {

	private final ParticipantRevisionRepository revisionRepository;

	/**
	 * Retrieves a participant's lap-time history, ordered from newest to oldest.
	 * <p>
	 * Only update revisions with a defined best lap time are included. Each entry
	 * contains the difference from the preceding eligible revision's lap time. The
	 * first entry in chronological order has a gap of {@link Duration#ZERO}.
	 *
	 * @param participantId the ID of the participant
	 * @return an immutable list of timeline entries, newest first; an empty list if
	 *         no eligible revisions exist
	 */
	@Transactional(readOnly = true)
	public List<ParticipantTimelineEntry> getTimeline(Integer participantId) {
		List<Participant> lapTimeRevisions = getParticipantLapTimeRevisions(participantId);

		if (lapTimeRevisions.isEmpty()) {
			return List.of();
		}
		return computeTimeline(lapTimeRevisions);
	}

	private List<Participant> getParticipantLapTimeRevisions(Integer participantId) {
		return revisionRepository.findRevisions(participantId).stream()
			// in our case, creation does never contain any lap time, nor deletion
			.filter(this::isUpdateOperation)
			// revision numbers provide a more reliable chronological order than modifiedAt
			.sorted(Comparator.comparingInt(Revision::getRequiredRevisionNumber))
			.map(Revision::getEntity)
			// gaps cannot be computed for revisions without a lap time
			.filter(this::hasLapTimeDefined)
			.toList();
	}

	private boolean isUpdateOperation(Revision<Integer, Participant> revision) {
		var revisionType = revision.getMetadata().getRevisionType();
		return revisionType == RevisionType.UPDATE;
	}

	private boolean hasLapTimeDefined(Participant participant) {
		return participant.getBestLapTime() != null;
	}

	private List<ParticipantTimelineEntry> computeTimeline(List<Participant> revisions) {
		List<ParticipantTimelineEntry> timeline = new ArrayList<>();
		// no previous lap exists for the first revision
		Duration previousLapTime = null;

		for (Participant participant : revisions) {
			Duration lapTime = participant.getBestLapTime();
			timeline.add(createTimelineEntry(participant, lapTime, previousLapTime));
			previousLapTime = lapTime;
		}
		// the newest revisions must come first
		Collections.reverse(timeline);
		return List.copyOf(timeline);
	}

	private ParticipantTimelineEntry createTimelineEntry(Participant participant, Duration lapTime, Duration previousLapTime) {
		return new ParticipantTimelineEntry(
			lapTime,
			participant.getModifiedAt(),
			calculateLapTimeGap(lapTime, previousLapTime)
		);
	}

	private Duration calculateLapTimeGap(Duration lapTime, Duration previousLapTime) {
		return previousLapTime != null
			? lapTime.minus(previousLapTime)
			: Duration.ZERO;
	}
}
