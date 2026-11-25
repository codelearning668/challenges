package sk.mkrajcovic.challenges.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationDetail;
import sk.mkrajcovic.challenges.model.NotificationConfiguration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationConfigurationMapper {

	public static NotificationConfigurationDetail toDetailResponse(NotificationConfiguration configuration) {
		return new NotificationConfigurationDetail(
			configuration.isNotifyOnChallengeCreated(),
			configuration.isNotifyOnChallengeClosed(),
			configuration.isNotifyOnChallengeLeaderChanged()
		);
	}
}
