package sk.mkrajcovic.challenges.model;

import java.time.Duration;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode(callSuper = false, exclude = {"challenge" })
public class Participant extends BaseEntity {

	/*
	 * This is essentially mapped to user name. Currently there is no intention to
	 * extend this for reference to the User entity, but keep the mind open
	 */
	@Column(nullable = false, length = 100)
	private String name;

	// because hibernate automatically tries to map this
	// field to PG numeric even DB definition data type is INTERVAL
	@JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
	private Duration bestLapTime;

	@ManyToOne
	private Challenge challenge;

}
