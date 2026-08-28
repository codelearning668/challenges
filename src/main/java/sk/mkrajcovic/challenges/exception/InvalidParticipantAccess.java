package sk.mkrajcovic.challenges.exception;

/**
 * Thrown when a user attempts to update data belonging to another participant.
 */
public class InvalidParticipantAccess extends AccessDenied {

	private static final long serialVersionUID = 1L;

	public InvalidParticipantAccess(String code) {
		super(code);
	}

	public InvalidParticipantAccess(String code, Object... args) {
		super(code, args);
	}
}
