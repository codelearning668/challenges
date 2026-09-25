package sk.mkrajcovic.challenges.model.read;

import java.math.BigDecimal;

import sk.mkrajcovic.challenges.enums.SimulatorType;

public interface TrackDetail {

	Integer getId();
	String getCountry();
	String getName();
	BigDecimal getLengthKm();
	SimulatorType getSimulatorType();

}
