package sk.mkrajcovic.challenges.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CarDetailResponse {

	private Integer id;
	private String brand;
	private String name;
	private Integer horsePower;
	private Integer torque;

}
