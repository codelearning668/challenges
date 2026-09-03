package sk.mkrajcovic.challenges.model.read;

import java.time.LocalDate;

public interface ChallengeDetail {

	Integer getId();
	LocalDate getEndDate();
	String getTrackName();
	String getTrackCountry();
	String getCarBrand();
	String getCarName();

}
