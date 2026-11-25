package sk.mkrajcovic.challenges.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationConfigurationUpdateRequest(

	@Schema(
		description = "Receive an email when a new challenge is created.",
		example = "true"
	)
	boolean notifyOnChallengeCreated,

	@Schema(
		description = "Receive an email when a challenge closes.",
		example = "false"
	)
	boolean notifyOnChallengeClosed,

	@Schema(
		description = "Receive an email when a challenge leader changes.",
		example = "true"
	)
	boolean notifyOnChallengeLeaderChanged

) { }
