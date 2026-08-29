package sk.mkrajcovic.challenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import static lombok.AccessLevel.NONE;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.util.Text;

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
