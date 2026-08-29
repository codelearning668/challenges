package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.model.WheelDrive;

@Getter @Setter
public class CreateCarRequest {

	@NotBlank @Size(max = 50)
	private String brand;

	@NotBlank @Size(max = 100)
	private String name;

	@Positive
	private Integer hp;

	@Positive
	private Integer torque;

	private WheelDrive drive; 

}
