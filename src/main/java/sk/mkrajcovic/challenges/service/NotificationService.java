package sk.mkrajcovic.challenges.service;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.mail.Email;
import sk.mkrajcovic.challenges.model.read.ChallengeDetail;
import sk.mkrajcovic.challenges.repository.persistence.ChallengeRepository;
import sk.mkrajcovic.challenges.repository.persistence.NotificationConfigurationRepository.NotificationRecipient;
import sk.mkrajcovic.challenges.util.ResourceLoader;

/**
 * Sends best-effort emails for configured application notification types.
 * <p>
 * Business services publish a concise event after completing an occurrence;
 * this service decides which users opted in, builds the email, and delegates
 * provider-independent delivery to {@link EmailService}.
 * <p>
 * This initial implementation deliberately has no delivery table or retry
 * mechanism. A failed provider call is logged and must never change the
 * outcome of the already-committed business operation.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

	private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

	private static final String CHALLENGE_CREATED_HTML_EMAIL_BODY_TEMPLATE =
		loadEmailNotificationTemplate("templates/notifications/email/challengeCreatedBody.html");

	private static final String CHALLENGE_CLOSED_HTML_EMAIL_BODY_TEMPLATE = 
		loadEmailNotificationTemplate("templates/notifications/email/challengeClosedBody.html");

	/*
	 * Spring's in-process publisher is deliberately private to notification
	 * orchestration. Callers express the business intent without depending on the
	 * event type or deferred-delivery mechanism.
	 */
	private final ApplicationEventPublisher applicationEventPublisher;
	/*
	 * NotificationService reads the scalar challenge projection needed to compose
	 * email content directly.
	 *
	 * Routing this query through ChallengeService would create a circular
	 * dependency because challenge creation already delegates here to schedule its
	 * notification. Breaking that cycle would require another coordinating/query
	 * component solely for this lookup, which is unnecessary complexity for the
	 * current application.
	 */
	private final ChallengeRepository challengeRepository;
	private final NotificationConfigurationService notificationConfigService;
	private final EmailService emailService;

	/**
	 * Registers new-challenge notification work for execution after the caller's
	 * transaction commits.
	 * <p>
	 * It does not send email or start an executor task now. Spring retains the
	 * internal event until it can invoke the listener at {@code AFTER_COMMIT}; when
	 * the caller rolls back, the listener is not invoked.
	 *
	 * @param challengeId newly created challenge ID
	 */
	public void notifyChallengeCreatedAfterCommit(Integer challengeId) {
		applicationEventPublisher.publishEvent(new ChallengeCreatedEvent(challengeId));
	}

	/**
	 * Sends the configured new-challenge email after its creating transaction
	 * successfully commits.
	 * <p>
	 * {@code AFTER_COMMIT} ensures recipients are never informed about a challenge
	 * that is later rolled back. {@code @Async} releases the creating request after
	 * commit and performs recipient lookup and Gmail delivery on the bounded
	 * {@code notificationExecutor} instead.
	 * <p>
	 * The event carries an ID rather than a challenge entity because the entity
	 * instance belongs to the transaction that published it.
	 *
	 * @param event newly committed challenge occurrence
	 */
	@Async("notificationExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	void notifyChallengeCreated(ChallengeCreatedEvent event) {
		List<NotificationRecipient> recipients = notificationConfigService.findChallengeCreatedRecipients();
		if (recipients.isEmpty()) {
			LOG.debug("No users enabled challenge-created email notifications");
			return;
		}
		ChallengeDetail challenge = findChallengeById(event.challengeId);
		Email notification = buildChallengeCreatedEmail(challenge, recipients);
		emailService.send(notification);

		LOG.info("Sent challenge-created email notification for challenge {} to {} recipient(s)", challenge.getId(),
				recipients.size());
	}

	private Email buildChallengeCreatedEmail(ChallengeDetail challenge, List<NotificationRecipient> recipients) {
		var emailBuilder = Email.builder()
			.subject(buildChallengeCreatedSubject(challenge))
			.textBody(buildChallengeCreatedBody(challenge));

		recipients.forEach(recipient -> emailBuilder.bcc(recipient.getEmailAddress()));
		return emailBuilder.build();
	}

	private String buildChallengeCreatedSubject(ChallengeDetail challenge) {
		return "🏎️ New %s challenge: %s %s at %s".formatted(
			challenge.getSimulatorType().getDisplayName(),
			challenge.getCarBrand(),
			challenge.getCarName(),
			challenge.getTrackName()
		);
	}

	private String buildChallengeCreatedBody(ChallengeDetail challenge) {
		return CHALLENGE_CREATED_HTML_EMAIL_BODY_TEMPLATE.formatted(
			escapeHtml(challenge.getSimulatorType().getDisplayName()),
			escapeHtml(challenge.getCarBrand()),
			escapeHtml(challenge.getCarName()),
			escapeHtml(challenge.getTrackName()),
			escapeHtml(challenge.getTrackCountry()),
			challenge.getEndDate(),
			challenge.getId()
		);
	}

	/**
	 * Sends the configured challenge-closed summary email.
	 * <p>
	 * The caller invokes this method after determining that the challenge has
	 * closed. Unlike new-challenge notifications, this operation is not deferred
	 * from a creating transaction. A delivery or lookup failure propagates to the
	 * caller.
	 *
	 * @param challengeId closed challenge ID
	 */
	public void notifyChallengeClosed(Integer challengeId) {
		List<NotificationRecipient> recipients = notificationConfigService.findChallengeClosedRecipients();
		if (recipients.isEmpty()) {
			LOG.debug("No users enabled challenge-closed email notifications for challenge {}", challengeId);
			return;
		}
		ChallengeDetail challenge = findChallengeById(challengeId);
		Email notification = buildClosedChallengeEmail(challenge, recipients);
		emailService.send(notification);

		LOG.info("Sent challenge-closed email notification for challenge {} to {} recipient(s)",
			challengeId, recipients.size());
	}

	private ChallengeDetail findChallengeById(Integer challengeId) {
		return challengeRepository.findChallengeDetailById(challengeId)
			// protecting invariants - this lookup should never fail
			.orElseThrow(() -> new IllegalStateException(
				"Committed challenge '%s' was not found".formatted(challengeId))
		);
	}

	private Email buildClosedChallengeEmail(ChallengeDetail challenge, List<NotificationRecipient> recipients) {
		var emailBuilder = Email.builder()
			.subject(buildClosedChallengeSubject(challenge))
			.textBody(buildClosedChallengeBody(challenge));

		recipients.forEach(recipient -> emailBuilder.to(recipient.getEmailAddress()));
		return emailBuilder.build();
	}

    private String buildClosedChallengeSubject(ChallengeDetail challenge) {
        return "🏁 Challenge finished: %s %s at %s".formatted(
            challenge.getCarBrand(),
            challenge.getCarName(),
            challenge.getTrackName()
        );
    }

	private String buildClosedChallengeBody(ChallengeDetail challenge) {
		final String bestDriverFallback = "No recorded lap time";

		String winner = StringUtils.defaultIfBlank(escapeHtml(challenge.getBestParticipantName()), bestDriverFallback);
		String bestLapTime = StringUtils.defaultIfBlank(formatLapTime(challenge.getBestLapTime()), bestDriverFallback);

		return CHALLENGE_CLOSED_HTML_EMAIL_BODY_TEMPLATE.formatted(
			escapeHtml(challenge.getSimulatorType().getDisplayName()),
			escapeHtml(challenge.getCarBrand()),
			escapeHtml(challenge.getCarName()),
			escapeHtml(challenge.getTrackName()),
			escapeHtml(challenge.getTrackCountry()),
			winner,
			bestLapTime,
			challenge.getId()
		);
	}

	private String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}

	private String formatLapTime(Duration lapTime) {
		if (lapTime == null) {
			return null;
		}
		long totalMillis = lapTime.toMillis();
		long minutes = totalMillis / 60_000;
		long seconds = (totalMillis % 60_000) / 1_000;
		long milliseconds = totalMillis % 1_000;

		return "%d:%02d.%03d".formatted(minutes, seconds, milliseconds);
	}

	private static String loadEmailNotificationTemplate(String templatePath) {
		try {
			return ResourceLoader.loadResource(templatePath).getContentAsString(UTF_8);
		} catch (IOException ioex) {
			throw new IllegalStateException("Could not load notification body template as string");
		}
	}

	/*
	 * This event is an implementation detail used only to connect the public
	 * after-commit registration method to this class's transactional listener.
	 * It remains nested because no other component should publish or consume it.
	 */
	record ChallengeCreatedEvent(Integer challengeId) {
	}
}
