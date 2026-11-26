package sk.mkrajcovic.challenges.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.model.NotificationConfiguration;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.repository.persistence.NotificationConfigurationRepository;
import sk.mkrajcovic.challenges.repository.persistence.NotificationConfigurationRepository.NotificationRecipient;

/**
 * Manages user-owned email-notification preference configurations.
 * <p>
 * This service owns preference persistence and default selection. It does not
 * construct or send notification emails; that responsibility belongs to
 * {@link NotificationService}.
 */
@Service
@RequiredArgsConstructor
public class NotificationConfigurationService {

	private static final Logger LOG = LoggerFactory.getLogger(NotificationConfigurationService.class);

	private final NotificationConfigurationRepository repository;
	private CallContext callContext;

	@Autowired
	void setCallContext(CallContext callContext) {
		this.callContext = callContext;
	}

	/**
	 * Creates the default notification configuration for a newly registered user.
	 * <p>
	 * New users are subscribed by default to email notifications when a challenge
	 * is created.
	 *
	 * @param user user that owns the new configuration
	 */
	@Transactional
	public void createDefaultConfiguration(User user) {
		var configuration = new NotificationConfiguration();
		configuration.setUser(user);
		configuration.setNotifyOnChallengeCreated(true);

		repository.save(configuration);

		LOG.info("Created default notification configuration for user '{}'", user.getUsername());
	}

	/**
	 * Retrieves the notification configuration owned by the authenticated user.
	 * <p>
	 * With current setup, configuration is created in the same registration
	 * transaction as its user, so its absence indicates a broken registration
	 * invariant.
	 *
	 * @return the authenticated user's existing notification configuration
	 * @throws IllegalStateException if the registration invariant is violated
	 */
	public NotificationConfiguration getNotificationConfiguration() {
		return getExistingNotificationConfiguration();
	}

	/**
	 * Replaces every known notification preference of the authenticated user.
	 * <p>
	 * Ownership is derived only from {@link CallContext}; callers cannot choose
	 * another user's configuration.
	 *
	 * @param notifyOnChallengeCreated receive new-challenge emails
	 * @param notifyOnChallengeClosed  receive challenge-closed emails
	 * @param notifyOnChallengeLeaderChanged receive leader-change emails
	 */
	@Transactional
	public void updateNotificationConfiguration(
			boolean notifyOnChallengeCreated,
			boolean notifyOnChallengeClosed) {

		var configuration = getExistingNotificationConfiguration();

		configuration.setNotifyOnChallengeCreated(notifyOnChallengeCreated);
		configuration.setNotifyOnChallengeClosed(notifyOnChallengeClosed);

		repository.save(configuration);

		LOG.info("Updated notification configuration for user '{}'",
			configuration.getUser().getUsername());
	}

	private NotificationConfiguration getExistingNotificationConfiguration() {
		String username = callContext.getCurrentUser().username();
		/*
		 * Missing configuration violates the registration invariant. It must not be
		 * silently recreated or hidden, that's why we throw runtime error instead of
		 * usual client error like ResourceNotFound.
		 */
		return repository.findByUserUsername(username)
			.orElseThrow(() -> new IllegalStateException(
				"Missing notification configuration for registered user '%s'".formatted(username)
		));
	}

	/**
	 * Finds enabled users that opted in to challenge-created email notifications.
	 *
	 * @return eligible email recipients, possibly empty
	 */
	List<NotificationRecipient> findChallengeCreatedRecipients() {
		return repository.findChallengeCreatedRecipients();
	}

	/**
	 * Finds enabled users that opted in to challenge-closed email notifications.
	 *
	 * @return eligible email recipients, possibly empty
	 */
	List<NotificationRecipient> findChallengeClosedRecipients() {
		return repository.findChallengeClosedRecipients();
	}

}
