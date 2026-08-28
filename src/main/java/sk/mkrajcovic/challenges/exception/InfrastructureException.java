package sk.mkrajcovic.challenges.exception;

/**
 * Represents a general application error saying that something unexpected
 * happened, and it should be used to wrap all checked exceptions.
 */
public class InfrastructureException extends GlobalException {

	private static final long serialVersionUID = 1L;

	public InfrastructureException(String code) {
		super(code, null, new Object[0]);
	}

	public InfrastructureException(String code, Object... args) {
		super(code, null, args);
	}

	public InfrastructureException(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
