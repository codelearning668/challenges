package sk.mkrajcovic.challenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Car extends BaseEntity {

	@Column(nullable = false, length = 50)
	private String brand;

	@Column(nullable = false, length = 100)
	private String name;

	// optional because of WRC and some cases in AC
	private Integer horsePower;
	private Integer torque;

	@Enumerated(EnumType.STRING)
	private WheelDrive wheelDrive;

}
