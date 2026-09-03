package sk.mkrajcovic.challenges.search;

import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.model.WheelDrive;

@Getter @Setter
public class SearchCarsCriteria {
	
	private String brand;
	private String name;

	// TODO review: accept ranges for these properties?
	private Integer horsePower;
	private Integer torque;

	private WheelDrive wheelDrive;

}
