package sk.mkrajcovic.challenges.exception;

/**
 * Thrown when a user attempts an action they are not permitted to perform.
 */
public class AccessDenied extends ClientException {

	private static final long serialVersionUID = 1L;

	public AccessDenied(String code) {
		super(code);
	}

	public AccessDenied(String code, Object... args) {
		super(code, args);
	}

	public AccessDenied(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
