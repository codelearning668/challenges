package sk.mkrajcovic.challenges.model;

import java.time.Duration;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an entry in a challenge's conversation.
 * <p>
 * Conversation entries allow participants to exchange short messages and engage
 * in friendly trash talk during an active challenge. Each entry preserves the
 * author's name and current lap time at the time of posting, ensuring that the
 * conversation remains a historical record even if the participant's
 * information subsequently changes.
 * <p>
 * The author is stored by name rather than through a {@link Participant}
 * association because the entry only needs to retain the author's identity for
 * display purposes.
 * <p>
 * Each entry belongs to a {@link Challenge}, which is required when the entry
 * is created.
 *
 * @see Challenge
 */
@Entity
@Getter @Setter
public class ConversationEntry extends BaseEntity {

	@Column(nullable = false, length = 1000)
	private String content;

	// represents the conversation author captured for display and history
	@Column(nullable = false, length = 100)
	private String participantName;

	/**
	 * The author's current lap time at the time of posting, if available.
	 * <p>
	 * This value is a snapshot and does not change when the participant's
	 * lap time changes later.
	 */
	@JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
	private Duration participantLapTimeAtPosting;

	/**
	 * The ID of the parent conversation entry, if this entry is a reply.
	 * <p>
	 * A {@code null} value indicates that the entry is not a reply. The parent is
	 * identified by its ID rather than a JPA relationship because the application
	 * only needs to store the reference and does not navigate the relationship
	 * through this entity.
	 * <p>
	 * The application is responsible for validating that the referenced entry
	 * exists and belongs to the same challenge.
	 */
	private Integer parentEntryId;

	/**
	 * This association is lazily fetched to avoid loading the challenge
	 * unnecessarily when retrieving conversation entries.
	 * <p>
	 * The challenge is expected to remain unchanged throughout the entry's
	 * lifecycle. The setter is available for entity construction and persistence
	 * workflows, but <b>reassignment should be avoided</b> because conversation
	 * entries are historical records belonging to a specific challenge.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Challenge challenge;

}

