package sk.mkrajcovic.challenges.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Challenge extends BaseEntity {

	// TODO: consider adding challenge title as every such event in real life has one

	@OneToMany(mappedBy = "challenge")
	private Set<Participant> participants;

	@ManyToOne
	private Track track;

	@ManyToOne
	private Car car;

	@Column(nullable = false)
	private LocalDate endDate;

}
