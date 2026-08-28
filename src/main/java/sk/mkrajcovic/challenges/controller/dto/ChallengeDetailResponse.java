package sk.mkrajcovic.challenges.controller.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChallengeDetailResponse {

	private Integer challengeId;
	private LocalDate challengeEndDate;

	private Integer trackId;
	private String trackName;
	private String trackCountry;
	private Double trackLengthKm;

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
