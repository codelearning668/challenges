package sk.mkrajcovic.challenges.model.read;

import sk.mkrajcovic.challenges.model.WheelDrive;

public interface CarDetail {

	Integer getId();
	String getBrand();
	String getName();
	Integer getHorsePower();
	Integer getTorque();
	WheelDrive getWheelDrive();

}