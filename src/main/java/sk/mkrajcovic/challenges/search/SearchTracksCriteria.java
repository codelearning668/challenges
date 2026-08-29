package sk.mkrajcovic.challenges.search;

import lombok.Getter;
import lombok.Setter;

// Note: setter methods need to be available for mapping filters
// otherwise it silently does not set anything (all fields = null)
@Getter @Setter
public class SearchTracksCriteria {

	private String country;
	private String name;

	// TODO review: support ranges?
	private Double lengthKm;

}
