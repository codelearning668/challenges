package sk.mkrajcovic.challenges.model.read;

import java.time.Duration;
import java.time.LocalDate;

public interface ChallengeDetail {

	Integer getId();
	LocalDate getEndDate();
	String getBestParticipantName();
	Duration getBestLapTime();
	String getTrackName();
	String getTrackCountry();
	String getCarBrand();
	String getCarName();

}
