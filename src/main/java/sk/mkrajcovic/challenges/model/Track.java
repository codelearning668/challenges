package sk.mkrajcovic.challenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Track extends BaseEntity {

	@Column(length = 100)
	private String country;

	@Column(nullable = false, length = 100)
	private String name;

	private Double lengthKm;
//	private TrackSurface surface;
}
