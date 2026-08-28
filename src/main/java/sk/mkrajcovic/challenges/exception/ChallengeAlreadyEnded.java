package sk.mkrajcovic.challenges.exception;

public class ChallengeAlreadyEnded extends BusinessViolation {

	private static final long serialVersionUID = 1L;

	public ChallengeAlreadyEnded(String code) {
		super(code);
	}

	public ChallengeAlreadyEnded(String code, Object... args) {
		super(code, args);
	}
}
