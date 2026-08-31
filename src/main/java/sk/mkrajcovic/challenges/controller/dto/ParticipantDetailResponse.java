package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

public record ParticipantDetailResponse(

	Integer participantId,
	String participantName,
	Duration participantBestLapTime

) { }
