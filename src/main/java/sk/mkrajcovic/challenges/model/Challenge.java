package sk.mkrajcovic.challenges.model;

import static lombok.AccessLevel.NONE;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import sk.mkrajcovic.challenges.util.Text;

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

	@Column(length = 100)
	private String bestParticipantName;

	@JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
	private Duration bestLapTime;

	@Column(length = 100)
	@Setter(NONE)
	@Getter(NONE)
	private String bestParticipantNameSearch;

	@PrePersist
	@PreUpdate
	private void runPreSaveOperations() {
		this.bestParticipantNameSearch = Text.normalizeForSearch(bestParticipantName);
	}
}
