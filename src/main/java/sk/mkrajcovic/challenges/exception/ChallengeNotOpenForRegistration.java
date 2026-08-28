package sk.mkrajcovic.challenges.exception;

public class ChallengeNotOpenForRegistration extends BusinessViolation {

	private static final long serialVersionUID = 1L;

	public ChallengeNotOpenForRegistration(String code) {
		super(code);
	}

	public ChallengeNotOpenForRegistration(String code, Object... args) {
		super(code, args);
	}
}
