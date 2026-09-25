package sk.mkrajcovic.challenges.model.read;

import java.time.Duration;
import java.time.LocalDate;

import sk.mkrajcovic.challenges.enums.SimulatorType;

public interface ChallengeDetail {

	Integer getId();
	LocalDate getEndDate();
	String getBestParticipantName();
	Duration getBestLapTime();
	SimulatorType getSimulatorType();
	String getTrackName();
	String getTrackCountry();
	String getCarBrand();
	String getCarName();

}
