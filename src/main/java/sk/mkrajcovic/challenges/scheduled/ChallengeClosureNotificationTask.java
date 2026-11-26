package sk.mkrajcovic.challenges.scheduled;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.model.read.ChallengeDetail;
import sk.mkrajcovic.challenges.search.SearchChallengesCriteria;
import sk.mkrajcovic.challenges.service.ChallengeService;
import sk.mkrajcovic.challenges.service.NotificationService;

@Component
@RequiredArgsConstructor
public class ChallengeClosureNotificationTask {

	private static final String EVERY_DAY_AT_1AM = "0 0 1 * * *";
	private static final String UTC = "UTC";

	private final ChallengeService challengeService;
	private final NotificationService notificationService;

	/**
	 * Notifies users about challenges that were closed yesterday.
	 */
	@Scheduled(cron = EVERY_DAY_AT_1AM, zone = UTC)
	public void processClosedChallengeNotifications() {
		List<ChallengeDetail> challenges = findChallengesClosedYesterday();
		notifyOnClosedChallenges(challenges);
	}

	private List<ChallengeDetail> findChallengesClosedYesterday() {
		var criteria = new SearchChallengesCriteria();
		criteria.setEndDate(LocalDate.now(ZoneOffset.UTC).minusDays(1));
		return challengeService.searchChallenges(criteria);
	}

	private void notifyOnClosedChallenges(List<ChallengeDetail> challenges) {
		challenges.stream()
			.map(ChallengeDetail::getId)
			.forEach(notificationService::notifyChallengeClosed);		
	}
}
