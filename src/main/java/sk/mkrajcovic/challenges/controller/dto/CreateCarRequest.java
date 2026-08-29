package sk.mkrajcovic.challenges.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import sk.mkrajcovic.challenges.model.WheelDrive;

public record CreateCarRequest(

	@NotBlank @Size(max = 50)
	String brand,

	@NotBlank @Size(max = 100)
	String name,

	@Positive
	Integer hp,

	@Positive
	Integer torque,

	WheelDrive drive

) { }
