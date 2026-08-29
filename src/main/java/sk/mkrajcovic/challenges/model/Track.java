package sk.mkrajcovic.challenges.model;

import static lombok.AccessLevel.NONE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.util.Text;

@Entity
@Getter @Setter
public class Track extends BaseEntity {

	@Column(length = 100)
	private String country;

	@Column(nullable = false, length = 100)
	private String name;

	private Double lengthKm;

	/*
	 * Denormalized search representation maintained
	 * automatically before persistence.
	 */
	@Getter(NONE)
	@Setter(NONE)
	@Column(length = 100)
	private String countrySearch;

	@Getter(NONE)
	@Setter(NONE)
	@Column(nullable = false, length = 100)
	private String nameSearch;

	@PrePersist
	@PreUpdate
	private void runPreSaveOperations() {
		countrySearch = Text.normalizeForSearch(country);
		nameSearch = Text.normalizeForSearch(name);
	}
}
