package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.model.read.ParticipantTimelineEntry;

class ParticipantTimelineMapperTest {

	@Test
	void mapsAnImprovementToSignedNegativeGap() {
		var response = ParticipantTimelineMapper.toResponse(
			new ParticipantTimelineEntry(Duration.ofSeconds(76), null, Duration.ofMillis(-502))
		);

		assertEquals(Duration.ofMillis(-502), response.gap());
	}

	@Test
	void mapsARegressionToSignedPositiveGap() {
		var response = ParticipantTimelineMapper.toResponse(
			new ParticipantTimelineEntry(Duration.ofSeconds(100), null, Duration.ofMillis(502))
		);

		assertEquals(Duration.ofMillis(502), response.gap());
	}
}
