package sk.mkrajcovic.challenges.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.RevisionMetadata.RevisionType;
import org.springframework.data.history.Revisions;

import sk.mkrajcovic.challenges.model.Participant;
import sk.mkrajcovic.challenges.model.read.ParticipantTimelineEntry;
import sk.mkrajcovic.challenges.repository.persistence.revision.ParticipantRevisionRepository;

class ParticipantTimelineServiceTest {

	private final ParticipantRevisionRepository revisionRepository = mock(ParticipantRevisionRepository.class);
	private final ParticipantTimelineService service = new ParticipantTimelineService(revisionRepository);

	@Test
	void returnsHistoricalTimesNewestFirstAndCalculatesProgressFromEarliest() {
		var earliest = participant(Duration.ofSeconds(100));
		var improved = participant(Duration.ofSeconds(98));
		var current = participant(Duration.ofSeconds(95));

		when(revisionRepository.findRevisions(7)).thenReturn(Revisions.of(List.of(
			revision(3, current, RevisionType.UPDATE),
			revision(1, earliest, RevisionType.UPDATE),
			revision(2, improved, RevisionType.UPDATE)
		)));

		assertEquals(List.of(
			new ParticipantTimelineEntry(Duration.ofSeconds(95), null, Duration.ofSeconds(-3)),
			new ParticipantTimelineEntry(Duration.ofSeconds(98), null, Duration.ofSeconds(-2)),
			new ParticipantTimelineEntry(Duration.ofSeconds(100), null, Duration.ZERO)
		), service.getTimeline(7));
	}

	@Test
	void keepsNonNullHistoricalTimesWhenLatestRevisionClearsCurrentTime() {
		var earliest = participant(Duration.ofSeconds(100));
		var improved = participant(Duration.ofSeconds(98));
		var cleared = participant(null);

		when(revisionRepository.findRevisions(7)).thenReturn(Revisions.of(List.of(
			revision(3, cleared, RevisionType.UPDATE),
			revision(1, earliest, RevisionType.UPDATE),
			revision(2, improved, RevisionType.UPDATE)
		)));

		assertEquals(List.of(
			new ParticipantTimelineEntry(Duration.ofSeconds(98), null, Duration.ofSeconds(-2)),
			new ParticipantTimelineEntry(Duration.ofSeconds(100), null, Duration.ZERO)
		), service.getTimeline(7));
	}

	@Test
	void ignoresInsertRevisionAndFiltersNullLapTimes() {
		var inserted = participant(Duration.ofSeconds(105));
		var earliest = participant(Duration.ofSeconds(100));
		var cleared = participant(null);
		var current = participant(Duration.ofSeconds(98));

		when(revisionRepository.findRevisions(7)).thenReturn(Revisions.of(List.of(
			revision(0, inserted, RevisionType.INSERT),
			revision(1, earliest, RevisionType.UPDATE),
			revision(2, cleared, RevisionType.UPDATE),
			revision(3, current, RevisionType.UPDATE)
		)));

		assertEquals(List.of(
			new ParticipantTimelineEntry(Duration.ofSeconds(98), null, Duration.ofSeconds(-2)),
			new ParticipantTimelineEntry(Duration.ofSeconds(100), null, Duration.ZERO)
		), service.getTimeline(7));
	}

	private static Participant participant(Duration lapTime) {
		var participant = new Participant();
		participant.setBestLapTime(lapTime);
		return participant;
	}

	private static Revision<Integer, Participant> revision(
		int revisionNumber,
		Participant participant,
		RevisionType revisionType
	) {
		RevisionMetadata<Integer> metadata = new RevisionMetadata<>() {
			@Override
			public Optional<Integer> getRevisionNumber() {
				return Optional.of(revisionNumber);
			}

			@Override
			public Optional<Instant> getRevisionInstant() {
				return Optional.empty();
			}

			@Override
		public <T> T getDelegate() {
			return null;
			}

			@Override
		public RevisionType getRevisionType() {
				return revisionType;
			}
		};

		return Revision.of(metadata, participant);
	}
}
