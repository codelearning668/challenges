package sk.mkrajcovic.challenges.exception;

/**
 * Exception representing a client error. Intended for use as validation errors
 * or any error tied to user input.
 */
public class ClientException extends GlobalException {

	private static final long serialVersionUID = -2229150692866750400L;

	public ClientException(String code) {
		super(code, null, new Object[0]);
	}

	public ClientException(String code, Object... args) {
		super(code, null, args);
	}

	public ClientException(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
