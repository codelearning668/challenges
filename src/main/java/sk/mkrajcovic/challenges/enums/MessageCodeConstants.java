package sk.mkrajcovic.challenges.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Centralized list of message codes.<br>
 * All of these are translated from definitions in
 * <code>messages.properties</code> by {@link MessageSource}
 *
 * @author mkrajcovicux
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageCodeConstants {

	public static final String UNEXPECTED_ERROR = "unexpectedError";
	public static final String STALE_UPDATE = "staleUpdate";
	public static final String RESOURCE_NOT_FOUND = "resourceNotFound";

	public static final String CANNOT_REGISTER_ON_CLOSED_CHALLENGE = "cannotRegisterOnClosedChallenge";
	public static final String PARTICIPANT_ALREADY_REGISTERED_FOR_CHALLENGE = "participantAlreadyRegisteredForChallenge";
	public static final String CANNOT_UPDATE_LAP_TIME_ON_CLOSED_CHALLENGE = "cannotUpdateLapTimeOnClosedChallenge";
	public static final String CANNOT_UPDATE_OTHER_PARTICIPANT_LAP_TIME = "cannotUpdateOtherParticipantLapTime";
	public static final String PARTICIPANT_NOT_REGISTERED_FOR_CHALLENGE = "participantNotRegisteredForChallenge";
	public static final String CHALLENGE_ALREADY_ACTIVE = "challengeAlreadyActive";

	public static final String USERNAME_ALREADY_TAKEN = "usernameAlreadyTaken";

}
