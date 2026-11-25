package sk.mkrajcovic.challenges.repository.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.NotificationConfiguration;

@Repository
public interface NotificationConfigurationRepository extends JpaRepository<NotificationConfiguration, Integer> {

	/**
	 * Finds enabled users that requested an email when a new challenge is created.
	 * <p>
	 * This projection deliberately selects only data required to address the
	 * message. It avoids loading notification-configuration and user entity graphs
	 * merely to create recipient list.
	 *
	 * @return eligible recipients, each with a non-blank email address
	 */
	@Query("""
        SELECT configuration.user.emailAddress AS emailAddress
        FROM NotificationConfiguration configuration
        WHERE configuration.notifyOnChallengeCreated = true
        AND configuration.user.enabled = true
        AND configuration.user.emailAddress IS NOT NULL
    """)
	List<ChallengeRecipient> findChallengeCreatedRecipients();

	/**
	 * Finds the unique notification configuration belonging to a user.
	 *
	 * @param username configuration owner's username
	 * @return that user's configuration when present
	 */
	Optional<NotificationConfiguration> findByUserUsername(String username);

	interface ChallengeRecipient {
		String getEmailAddress();
	}
}
