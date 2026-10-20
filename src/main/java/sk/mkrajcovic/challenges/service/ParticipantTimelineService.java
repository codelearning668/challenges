package sk.mkrajcovic.challenges.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
//TODO: refactooooooooooooooooor!!!
	// committed in a hurry so that the UI can implement this feature..
	@Transactional(readOnly = true)
	public List<ParticipantTimelineEntry> getTimeline(Integer participantId) {
		
		List<Revision<Integer, Participant>> revisions = revisionRepository.findRevisions(participantId).stream()
			.filter(this::isUpdateOperation)
			.sorted(Comparator.comparingInt(Revision::getRequiredRevisionNumber))
			.collect(Collectors.toList());

		if (revisions.isEmpty()) {
			return Collections.emptyList();
		}

		// a non-null latest value is current; a null latest value records that the time was removed
		// TODO: do not remove the last time
//		if (!revisions.isEmpty() && revisions.getLast().getEntity().getBestLapTime() != null) {
//			revisions.removeLast();
//		}

		List<Participant> historicalTimes = revisions.stream()
			.map(Revision::getEntity)
			.filter(participant -> participant.getBestLapTime() != null)
			.toList();

		List<ParticipantTimelineEntry> timeline = new ArrayList<>();
		Duration previousLapTime = null;

		for (Participant participant : historicalTimes) {
			Duration lapTime = participant.getBestLapTime();
			Duration signedGap = previousLapTime == null
				? Duration.ZERO
				: lapTime.minus(previousLapTime);

			timeline.add(new ParticipantTimelineEntry(
				lapTime,
				participant.getModifiedAt(),
				signedGap
			));
			previousLapTime = lapTime;
		}

		Collections.reverse(timeline);
		return List.copyOf(timeline);
	}

	private boolean isUpdateOperation(Revision<Integer, Participant> revision) {
		var revisionType = revision.getMetadata().getRevisionType();
		return revisionType == RevisionType.UPDATE;
	}
}
