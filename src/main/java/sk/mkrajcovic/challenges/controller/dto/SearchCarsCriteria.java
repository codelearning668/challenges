package sk.mkrajcovic.challenges.controller.dto;

import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.model.WheelDrive;

@Getter @Setter
public class SearchCarsCriteria {
	
	private String brand;
	private String name;

	// TODO review: accept ranges for these properties?
	private String horsePower;
	private String torque;

	private WheelDrive wheelDrive;

}
