package sk.mkrajcovic.challenges.controller.dto;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ParticipantDetailResponse {

	private Integer participantId;
	private String participantName;
	private Duration participantBestLapTime;

}
