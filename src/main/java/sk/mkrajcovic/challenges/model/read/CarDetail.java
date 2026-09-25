package sk.mkrajcovic.challenges.model.read;

import sk.mkrajcovic.challenges.model.WheelDrive;
import sk.mkrajcovic.challenges.enums.SimulatorType;

public interface CarDetail {

	Integer getId();
	String getBrand();
	String getName();
	Integer getHorsePower();
	Integer getTorque();
	WheelDrive getWheelDrive();
	SimulatorType getSimulatorType();

}