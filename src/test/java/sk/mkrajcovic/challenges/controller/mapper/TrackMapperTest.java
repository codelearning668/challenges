package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.controller.dto.CreateTrackRequest;
import sk.mkrajcovic.challenges.model.Track;
import sk.mkrajcovic.challenges.model.read.TrackDetail;
import sk.mkrajcovic.challenges.test.util.EntityTestUtils;

class TrackMapperTest {

	@Test
	void shouldMapTrackToDetailResponse() {
		var track = new Track();
		EntityTestUtils.setId(track, 42);
		track.setName("Slovakia Ring");
		track.setCountry("Slovakia");
		track.setLengthKm(5.922);

		var response = TrackMapper.toDetailResponse(track);

		assertAll(
			() -> assertEquals(42, response.id()),
			() -> assertEquals("Slovakia Ring", response.name()),
			() -> assertEquals("Slovakia", response.country()),
			() -> assertEquals(5.922, response.lengthKm()));
	}

	@Test
	void shouldRejectNullTrackWhenMappingToDetailResponse() {
		var exception = assertThrows(NullPointerException.class, () -> TrackMapper.toDetailResponse((Track)null));
		assertEquals("track cannot be null in order to map its values", exception.getMessage());
	}

	@Test
	void shouldMapTrackDetailToDetailResponse() {
		var trackDetail = new TrackDetail() {

			@Override
			public Integer getId() {
				return 42;
			}

			@Override
			public String getCountry() {
				return "Slovakia";
			}

			@Override
			public String getName() {
				return "Slovakia Ring";
			}

			@Override
			public Double getLengthKm() {
				return 5.922;
			}
		};

		var response = TrackMapper.toDetailResponse(trackDetail);

		assertAll(
			() -> assertEquals(42, response.id()),
			() -> assertEquals("Slovakia Ring", response.name()),
			() -> assertEquals("Slovakia", response.country()),
			() -> assertEquals(5.922, response.lengthKm()));
	}

	@Test
	void shouldRejectNullTrackDetailWhenMappingToDetailResponse() {
		var exception = assertThrows(
			NullPointerException.class,
			() -> TrackMapper.toDetailResponse((TrackDetail) null));

		assertEquals("trackDetail cannot be null in order to map its values", exception.getMessage());
	}

	@Test
	void shouldMapCreateTrackRequestToTrack() {
		var request = new CreateTrackRequest("Slovakia Ring", "Slovakia", 5.922);
		var track = TrackMapper.toTrack(request);

		assertAll(
			() -> assertEquals("Slovakia Ring", track.getName()),
			() -> assertEquals("Slovakia", track.getCountry()),
			() -> assertEquals(5.922, track.getLengthKm()));
	}

	@Test
	void shouldRejectNullCreateTrackRequest() {
		var exception = assertThrows(NullPointerException.class, () -> TrackMapper.toTrack(null));
		assertEquals("input request cannot be null in order to map its values", exception.getMessage());
	}
}
