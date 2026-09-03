package sk.mkrajcovic.challenges.search;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SearchTracksCriteria {

	private String country;
	private String name;

	// TODO review: support ranges?
	private Double lengthKm;

}
