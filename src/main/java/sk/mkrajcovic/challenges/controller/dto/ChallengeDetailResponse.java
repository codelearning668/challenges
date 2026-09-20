package sk.mkrajcovic.challenges.controller.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Flat response DTO for challenge details.
 *
 * <p>
 * This DTO intentionally remains a class rather than a record. Its flat
 * structure requires many values from different parts of the domain model.
 * Using a record would result in a large constructor, making the mapper less
 * readable and increasing the risk of accidentally swapping parameters.
 */
@Getter @Setter
public class ChallengeDetailResponse {

	private Integer challengeId;
	private LocalDate challengeEndDate;
	private String bestParticipantName;

	@Schema(
		description = "Quickest recorded lap time, returned in canonical mm:ss.SSS format.",
		type = "string",
		pattern = "^\\d{2}:\\d{2}\\.\\d{3}$",
		example = "01:20.100",
		nullable = true
	)
	private Duration bestLapTime;

	private Integer trackId;
	private String trackName;
	private String trackCountry;
	private BigDecimal trackLengthKm;

	private Integer carId;
	private String carBrand;
	private String carName;
	private Integer carHorsePower;
	private Integer carTorque;

	@Setter(AccessLevel.NONE)
	private List<ParticipantDetailResponse> participants;

	public ChallengeDetailResponse() {
		participants = new ArrayList<>();
	}
}
