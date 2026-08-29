package sk.mkrajcovic.challenges.controller.dto;

public record CarDetailResponse (
	Integer id,
	String brand,
	String name,
	Integer horsePower,
	Integer torque
) { }
