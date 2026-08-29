package sk.mkrajcovic.challenges.controller.dto;

public record TrackDetailResponse(

	Integer id,
	String country,
	String name,
	Double lengthKm,
	Integer version

) { }