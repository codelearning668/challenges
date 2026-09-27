package sk.mkrajcovic.challenges.exception;

/** Exception indicating that an operation violates a business rule. */
public class BusinessViolation extends ClientException {

	private static final long serialVersionUID = 1L;

	public BusinessViolation(String code) {
		super(code);
	}

	public BusinessViolation(String code, Object... args) {
		super(code, args);
	}

	public BusinessViolation(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
