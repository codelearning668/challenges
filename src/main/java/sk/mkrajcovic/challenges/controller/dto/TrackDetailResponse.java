package sk.mkrajcovic.challenges.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TrackDetailResponse {

	private Integer id;
	private String country;
	private String name;
	private Double lengthKm;

}
