package sk.mkrajcovic.challenges.model;

import jakarta.persistence.*;

import static lombok.AccessLevel.NONE;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.util.Text;

import java.util.Set;

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

	@ManyToOne(optional = false)
	private Simulator simulator;

	@Column(nullable = false)
	boolean fromDlc;

	/*
	 * Denormalized search representation maintained
	 * automatically before persistence.
	 */
	@Getter(NONE)
	@Setter(NONE)
	@Column(nullable = false, length = 50)
	private String brandSearch;

	@Getter(NONE)
	@Setter(NONE)
	@Column(nullable = false, length = 100)
	private String nameSearch;

	@PrePersist
	@PreUpdate
	private void runPreSaveOperations() {
		brandSearch = Text.normalizeForSearch(brand);
		nameSearch = Text.normalizeForSearch(name);
	}
}
