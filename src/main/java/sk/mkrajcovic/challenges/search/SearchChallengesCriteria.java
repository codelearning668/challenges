package sk.mkrajcovic.challenges.search;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SearchChallengesCriteria {

	// TODO review: provide range, or filter only by isActive?
	private LocalDate endDate;
	private String bestParticipantName;

	private String trackName;
	private String trackCountry;
	private String carBrand;
	private String carName;
}
