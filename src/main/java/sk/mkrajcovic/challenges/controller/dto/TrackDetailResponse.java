package sk.mkrajcovic.challenges.controller.dto;

import java.math.BigDecimal;

public record TrackDetailResponse(

	Integer id,
	String country,
	String name,
	BigDecimal lengthKm,
	String simulatorName

) { }