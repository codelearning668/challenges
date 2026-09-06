package sk.mkrajcovic.challenges.controller.dto;

import sk.mkrajcovic.challenges.model.WheelDrive;

public record CarDetailResponse (

	Integer id,
	String brand,
	String name,
	Integer horsePower,
	Integer torque,
	WheelDrive wheelDrive

) { }
