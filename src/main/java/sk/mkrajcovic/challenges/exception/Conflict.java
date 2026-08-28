package sk.mkrajcovic.challenges.exception;

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
