package sk.mkrajcovic.challenges.exception;

/**
 * Exception indicating that an operation cannot be completed because it
 * conflicts with the current state of the application or a resource.
 */
public class Conflict extends ClientException {

	private static final long serialVersionUID = 1L;

	public Conflict(String code) {
		super(code);
	}

	public Conflict(String code, Object... args) {
		super(code, args);
	}

	public Conflict(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
