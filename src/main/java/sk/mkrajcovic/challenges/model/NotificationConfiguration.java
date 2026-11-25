package sk.mkrajcovic.challenges.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Stores a user's enabled email-notification categories.
 * <p>
 * Exactly one configuration belongs to one user. The configuration is evaluated
 * when selecting recipients for a notification; it does not represent an email
 * that has already been scheduled or sent.
 */
@Entity
@Getter @Setter
public class NotificationConfiguration extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "username", nullable = false, unique = true)
	private User user;

	@Column(nullable = false)
	private boolean notifyOnChallengeCreated;

	@Column(nullable = false)
	private boolean notifyOnChallengeClosed;

	@Column(nullable = false)
	private boolean notifyOnChallengeLeaderChanged;

}
