package sk.mkrajcovic.challenges.controller.dto;

public record NotificationConfigurationDetail(

	boolean notifyOnChallengeCreated,
	boolean notifyOnChallengeClosed,
	boolean notifyOnChallengeLeaderChanged

)
{ }
